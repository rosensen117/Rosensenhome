package com.shiguang.lostfound.storage;

import com.shiguang.lostfound.auth.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
public class ObjectStorageService {
    private static final long MAX_SIZE = 5L * 1024 * 1024;
    private final S3Client s3;
    private final StorageProperties properties;

    public ObjectStorageService(S3Client s3, StorageProperties properties) {
        this.s3 = s3;
        this.properties = properties;
    }

    public StoredImage uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new ApiException(HttpStatus.BAD_REQUEST, "请选择要上传的图片");
        if (file.getSize() > MAX_SIZE) throw new ApiException(HttpStatus.BAD_REQUEST, "单张图片不能超过5MB");
        try {
            byte[] bytes = file.getBytes();
            ImageType type = detectType(bytes);
            ensureBucket();
            LocalDate now = LocalDate.now();
            String key = "items/%d/%02d/%s.%s".formatted(now.getYear(), now.getMonthValue(), UUID.randomUUID(), type.extension);
            s3.putObject(PutObjectRequest.builder()
                            .bucket(properties.bucket())
                            .key(key)
                            .contentType(type.contentType)
                            .contentLength((long) bytes.length)
                            .cacheControl("public, max-age=31536000, immutable")
                            .build(),
                    RequestBody.fromBytes(bytes));
            return new StoredImage(key, publicUrl(key), type.contentType, bytes.length);
        } catch (IOException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "图片读取失败");
        } catch (S3Exception ex) {
            if (ex.statusCode() == 401 || ex.statusCode() == 403) {
                throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "Supabase对象存储密钥无效或已撤销，请更新S3 Access Key");
            }
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "对象存储暂时不可用，请检查存储配置后重试");
        }
    }

    public StoredObject download(String key) {
        try {
            var response = s3.getObject(GetObjectRequest.builder().bucket(properties.bucket()).key(key).build());
            return new StoredObject(response.response().contentType(), response.readAllBytes());
        } catch (Exception ex) {
            throw new ApiException(HttpStatus.NOT_FOUND, "图片不存在");
        }
    }

    private void ensureBucket() {
        try {
            s3.headBucket(HeadBucketRequest.builder().bucket(properties.bucket()).build());
        } catch (S3Exception ex) {
            if (ex.statusCode() == 404) s3.createBucket(CreateBucketRequest.builder().bucket(properties.bucket()).build());
            else throw ex;
        }
    }

    private String publicUrl(String key) {
        return properties.publicUrl().replaceAll("/+$", "") + "/" + key;
    }

    private ImageType detectType(byte[] bytes) {
        if (bytes.length >= 3 && (bytes[0] & 0xff) == 0xff && (bytes[1] & 0xff) == 0xd8 && (bytes[2] & 0xff) == 0xff)
            return new ImageType("jpg", "image/jpeg");
        if (bytes.length >= 8 && (bytes[0] & 0xff) == 0x89 && bytes[1] == 0x50 && bytes[2] == 0x4e && bytes[3] == 0x47)
            return new ImageType("png", "image/png");
        if (bytes.length >= 12 && bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F'
                && bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P')
            return new ImageType("webp", "image/webp");
        throw new ApiException(HttpStatus.BAD_REQUEST, "仅支持 JPG、PNG、WebP 图片");
    }

    private record ImageType(String extension, String contentType) {}
    public record StoredObject(String contentType, byte[] bytes) {}
}

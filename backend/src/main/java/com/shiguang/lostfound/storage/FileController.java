package com.shiguang.lostfound.storage;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final ObjectStorageService storage;

    public FileController(ObjectStorageService storage) { this.storage = storage; }

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StoredImage upload(@RequestPart("file") MultipartFile file) {
        return storage.uploadImage(file);
    }

    @GetMapping("/public/{*key}")
    public ResponseEntity<byte[]> publicImage(@PathVariable String key) {
        var object = storage.download(key.replaceFirst("^/", ""));
        MediaType mediaType = MediaType.parseMediaType(object.contentType());
        return ResponseEntity.ok().contentType(mediaType)
                .cacheControl(CacheControl.maxAge(java.time.Duration.ofDays(365)).cachePublic())
                .body(object.bytes());
    }
}

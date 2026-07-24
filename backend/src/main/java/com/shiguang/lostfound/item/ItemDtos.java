package com.shiguang.lostfound.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public final class ItemDtos {
    private ItemDtos() {}

    public record CreateItemRequest(
            @NotBlank(message = "请选择信息类型")
            @Pattern(regexp = "lost|found", message = "信息类型不正确") String type,
            @NotBlank(message = "请填写物品标题") @Size(max = 30, message = "标题不能超过30个字") String title,
            @NotBlank(message = "请选择物品分类") @Size(max = 30) String category,
            @NotNull(message = "请选择日期") LocalDate eventDate,
            @NotBlank(message = "请填写地点") @Size(max = 120, message = "地点不能超过120个字") String location,
            @NotBlank(message = "请填写公开描述") @Size(max = 300, message = "描述不能超过300个字") String description,
            @Size(max = 300, message = "隐藏特征不能超过300个字") String hiddenFeature,
            @Size(max = 6, message = "最多上传6张图片") List<@Valid ImageRequest> images
    ) {}

    public record ImageRequest(
            @NotBlank(message = "图片对象标识不能为空") @Size(max = 300) String key,
            @NotBlank(message = "图片地址不能为空") @Size(max = 600) String url
    ) {}

    public record ItemResponse(
            Long id,
            String type,
            String title,
            String category,
            LocalDate eventDate,
            String location,
            String description,
            String status,
            Instant createdAt,
            String publisher,
            boolean publisherVerified,
            List<ImageResponse> images
    ) {}

    public record ImageResponse(String key, String url, int sortOrder) {}
}

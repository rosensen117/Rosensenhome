package com.shiguang.lostfound.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static com.shiguang.lostfound.item.ItemDtos.ImageRequest;
import static com.shiguang.lostfound.item.ItemDtos.ImageResponse;

public final class DraftDtos {
    private DraftDtos() {}

    public record SaveDraftRequest(
            @Pattern(regexp = "lost|found") String type,
            @Size(max = 30) String title,
            @Size(max = 30) String category,
            LocalDate eventDate,
            @Size(max = 120) String location,
            @Size(max = 300) String description,
            @Size(max = 300) String hiddenFeature,
            @Size(max = 6) List<@Valid ImageRequest> images
    ) {}

    public record DraftResponse(Long id, String type, String title, String category, LocalDate eventDate,
                                String location, String description, String hiddenFeature,
                                List<ImageResponse> images, Instant createdAt, Instant updatedAt) {}
}

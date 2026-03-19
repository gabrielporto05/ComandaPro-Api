package me.gabrielporto.comandapro.adapters.in.web.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        UUID storeId,
        String name,
        String description,
        Double price,
        String photoUrl,
        String category,
        Boolean isAvailable,
        Boolean isHighlight,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

package me.gabrielporto.comandapro.infrastructure.web.dto.response;

import java.time.LocalDateTime;
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
        LocalDateTime createdAt,
        LocalDateTime updatedAt
        ) {

}

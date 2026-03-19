package me.gabrielporto.comandapro.adapters.in.web.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record StoreResponse(
        UUID id,
        String name,
        String slug,
        UUID ownerId,
        String email,
        String tel,
        String status,
        String description,
        String address,
        List<String> photos,
        BigDecimal feeDelivery,
        List<String> categories,
        List<String> paymentMethods,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        int productsCount,
        List<HoursResponse> hours
) {
}

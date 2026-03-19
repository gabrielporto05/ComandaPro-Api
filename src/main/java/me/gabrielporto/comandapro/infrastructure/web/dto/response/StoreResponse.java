package me.gabrielporto.comandapro.infrastructure.web.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record StoreResponse(
        UUID id,
        String name,
        String slug,
        UUID userId,
        String email,
        String tel,
        String status,
        String description,
        String address,
        List<String> photos,
        Double feeDelivery,
        List<String> categories,
        List<String> paymentMethods,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer productCount,
        List<HoursResponse> horarios
        ) {

}

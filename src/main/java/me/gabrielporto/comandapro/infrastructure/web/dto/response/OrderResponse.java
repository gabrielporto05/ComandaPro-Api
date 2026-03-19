package me.gabrielporto.comandapro.infrastructure.web.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String orderCode,
        UUID storeId,
        String customerName,
        String customerPhone,
        String customerAddress,
        String customerAddressNumber,
        String customerAddressComplement,
        String customerAddressNeighborhood,
        String customerAddressCity,
        String customerAddressZipcode,
        String paymentMethod,
        Double changeFor,
        Double totalAmount,
        Double deliveryFee,
        String generalObservation,
        String status,
        Integer preparationTimeMinutes,
        LocalDateTime acceptedAt,
        LocalDateTime readyAt,
        LocalDateTime deliveredAt,
        LocalDateTime cancelledAt,
        String cancellationReason,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
        ) {

}

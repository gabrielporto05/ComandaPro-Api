package me.gabrielporto.comandapro.adapters.in.web.dto.response;

import java.time.OffsetDateTime;
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
        OffsetDateTime acceptedAt,
        OffsetDateTime readyAt,
        OffsetDateTime deliveredAt,
        OffsetDateTime cancelledAt,
        String cancellationReason,
        OffsetDateTime createdAt,
        List<OrderItemResponse> items
) {
}

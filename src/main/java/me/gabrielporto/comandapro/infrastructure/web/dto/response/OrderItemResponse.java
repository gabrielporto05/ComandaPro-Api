package me.gabrielporto.comandapro.infrastructure.web.dto.response;

import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        UUID productId,
        String productName,
        Integer quantity,
        Double unitPrice,
        Double totalPrice,
        String observation
        ) {

}

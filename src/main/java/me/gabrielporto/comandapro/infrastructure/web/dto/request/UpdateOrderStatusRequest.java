package me.gabrielporto.comandapro.infrastructure.web.dto.request;

import jakarta.validation.constraints.NotNull;
import me.gabrielporto.comandapro.core.domain.order.OrderStatus;

public record UpdateOrderStatusRequest(
        @NotNull(message = "Status é obrigatório")
        OrderStatus status,
        String cancellationReason
        ) {

}

package me.gabrielporto.comandapro.adapters.in.web.dto.request;

import jakarta.validation.constraints.NotNull;
import me.gabrielporto.comandapro.core.domain.order.OrderStatus;

public record UpdateOrderStatusRequest(
        @NotNull OrderStatus status,
        String cancellationReason
) {
}

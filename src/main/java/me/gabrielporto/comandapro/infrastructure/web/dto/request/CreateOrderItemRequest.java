package me.gabrielporto.comandapro.infrastructure.web.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record CreateOrderItemRequest(
        @NotNull(message = "ID do produto é obrigatório")
        UUID productId,
        @NotNull(message = "Quantidade é obrigatória")
        @Positive(message = "Quantidade deve ser maior que zero")
        Integer quantity,
        String observation
        ) {

}

package me.gabrielporto.comandapro.infrastructure.web.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import me.gabrielporto.comandapro.core.domain.order.PaymentMethod;

public record CreateOrderRequest(
        @NotBlank(message = "Nome do cliente é obrigatório")
        @Size(max = 255)
        String customerName,
        @NotBlank(message = "Telefone do cliente é obrigatório")
        @Size(max = 20)
        String customerPhone,
        @NotBlank(message = "Endereço é obrigatório")
        @Size(max = 255)
        String customerAddress,
        @Size(max = 10)
        String customerAddressNumber,
        @Size(max = 100)
        String customerAddressComplement,
        @Size(max = 100)
        String customerAddressNeighborhood,
        @NotNull(message = "Forma de pagamento é obrigatória")
        PaymentMethod paymentMethod,
        Double changeFor,
        @Valid
        @NotEmpty(message = "O pedido deve ter pelo menos um item")
        List<CreateOrderItemRequest> items,
        @Size(max = 500)
        String generalObservation
        ) {

}

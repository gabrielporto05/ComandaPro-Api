package me.gabrielporto.comandapro.adapters.in.web.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import me.gabrielporto.comandapro.core.domain.order.PaymentMethod;

public record CreateOrderRequest(
        @NotBlank String customerName,
        @NotBlank String customerPhone,
        String customerAddress,
        String customerAddressNumber,
        String customerAddressComplement,
        String customerAddressNeighborhood,
        String customerAddressCity,
        String customerAddressZipcode,
        @NotNull PaymentMethod paymentMethod,
        Double changeFor,
        String generalObservation,
        @NotEmpty @Valid List<CreateOrderItemRequest> items,
        @Min(0) Double deliveryFee
) {
}

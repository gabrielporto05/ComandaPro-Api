package me.gabrielporto.comandapro.adapters.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateStoreRequest(
        @NotBlank String name,
        String email,
        String tel,
        String description,
        String address,
        String[] photos,
        Double feeDelivery
) {
}

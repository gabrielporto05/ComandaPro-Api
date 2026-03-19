package me.gabrielporto.comandapro.adapters.in.web.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
        @NotNull @Valid Plan plan,
        @NotNull @Valid ClientData client,
        @NotNull @Valid StoreData store
) {
    public record Plan(@NotBlank String name, @NotBlank String periodo, @NotNull Double preco) {
    }

    public record ClientData(@NotBlank String name, @NotBlank @Email String email, @NotBlank String tel, @NotBlank String password) {
    }

    public record StoreData(@NotBlank String store_name) {
    }
}

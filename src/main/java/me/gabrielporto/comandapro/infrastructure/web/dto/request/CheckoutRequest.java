package me.gabrielporto.comandapro.infrastructure.web.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import me.gabrielporto.comandapro.core.domain.subscription.PlanType;

public record CheckoutRequest(
        @NotNull(message = "Plano é obrigatório")
        PlanType plan,
        @NotNull(message = "Dados do cliente são obrigatórios")
        @Valid
        ClientRequest client,
        @NotNull(message = "Dados da loja são obrigatórios")
        @Valid
        StoreRequest store
        ) {

    public record ClientRequest(
            @NotBlank(message = "Nome é obrigatório")
            String name,
            @NotBlank(message = "Email é obrigatório")
            @jakarta.validation.constraints.Email(message = "Email inválido")
            String email,
            @NotBlank(message = "Telefone é obrigatório")
            @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}", message = "Telefone deve estar no formato (99) 99999-9999")
            String tel,
            @NotBlank(message = "Senha é obrigatória")
            @jakarta.validation.constraints.Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
            String password
            ) {

    }

    public record StoreRequest(
            @NotBlank(message = "Nome da loja é obrigatório")
            String store_name
            ) {

    }
}

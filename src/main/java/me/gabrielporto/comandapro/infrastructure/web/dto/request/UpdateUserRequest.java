package me.gabrielporto.comandapro.infrastructure.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        String name,
        @Email(message = "Email inválido")
        String email,
        @Size(min = 6, message = "Senha nova deve ter no mínimo 6 caracteres")
        String newPassword,
        String currentPassword
        ) {

}

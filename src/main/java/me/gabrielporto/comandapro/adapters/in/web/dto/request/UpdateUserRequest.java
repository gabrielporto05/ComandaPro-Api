package me.gabrielporto.comandapro.adapters.in.web.dto.request;

import jakarta.validation.constraints.Email;

public record UpdateUserRequest(
        String name,
        @Email String email,
        String currentPassword,
        String newPassword
) {
}

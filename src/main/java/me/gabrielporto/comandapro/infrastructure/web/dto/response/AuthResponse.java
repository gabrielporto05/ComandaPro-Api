package me.gabrielporto.comandapro.infrastructure.web.dto.response;

public record AuthResponse(
        String access_token,
        UserResponse user
        ) {

}

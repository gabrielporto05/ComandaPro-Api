package me.gabrielporto.comandapro.adapters.in.web.dto;

import me.gabrielporto.comandapro.core.domain.user.User;

public record AuthResponse(String token, UserResponse user) {

    public static AuthResponse of(String token, User user) {
        return new AuthResponse(token, UserResponse.from(user));
    }
}

package me.gabrielporto.comandapro.adapters.in.web.dto;

import java.util.UUID;

import me.gabrielporto.comandapro.core.domain.user.User;

public record UserResponse(UUID id, String name, String email, String tel, String role, String status) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getTel(), user.getRole().name(), user.getStatus().name());
    }
}

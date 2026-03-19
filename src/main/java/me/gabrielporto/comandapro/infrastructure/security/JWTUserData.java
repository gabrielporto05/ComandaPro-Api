package me.gabrielporto.comandapro.infrastructure.security;

import java.util.UUID;

import lombok.Builder;

@Builder
public record JWTUserData(UUID id, String email) {

}

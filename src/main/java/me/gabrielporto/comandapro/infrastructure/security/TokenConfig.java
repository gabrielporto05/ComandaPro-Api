package me.gabrielporto.comandapro.infrastructure.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import me.gabrielporto.comandapro.core.domain.user.User;

@Component
public class TokenConfig {

    @Value("${jwt.secret:mySuperSecretKey12345}")
    private String secretKey;

    @Value("${jwt.expiration:3600}")
    private long expirationSeconds;

    @Value("${jwt.issuer:comandapro}")
    private String issuer;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secretKey);
    }

    public String generateToken(User user) {
        return JWT.create()
                .withIssuer(issuer)
                .withClaim("userId", user.getId().toString())
                .withSubject(user.getEmail())
                .withExpiresAt(getExpirationDate())
                .withIssuedAt(Instant.now())
                .sign(getAlgorithm());
    }

    public Optional<JWTUserData> validateToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(getAlgorithm())
                    .withIssuer(issuer)
                    .build()
                    .verify(token);

            String userId = decodedJWT.getClaim("userId").asString();
            String email = decodedJWT.getSubject();

            return Optional.of(
                    JWTUserData.builder()
                            .id(UUID.fromString(userId))
                            .email(email)
                            .build());

        } catch (JWTVerificationException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Instant getExpirationDate() {
        return LocalDateTime.now()
                .plusSeconds(expirationSeconds)
                .toInstant(ZoneOffset.of("-03:00")); // Horário de Brasília
    }
}

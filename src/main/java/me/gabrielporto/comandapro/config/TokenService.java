package me.gabrielporto.comandapro.config;

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

import me.gabrielporto.comandapro.core.domain.user.User;

@Component
public class TokenService {

    @Value("${jwt.secret:change-me}")
    private String secretKey;

    @Value("${jwt.expiration:3600}")
    private long expirationSeconds;

    @Value("${jwt.issuer:ComandaPro}")
    private String issuer;

    private Algorithm algorithm() {
        return Algorithm.HMAC256(secretKey);
    }

    public String generate(User user) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(user.getEmail())
                .withClaim("userId", user.getId().toString())
                .withExpiresAt(expiration())
                .withIssuedAt(Instant.now())
                .sign(algorithm());
    }

    public Optional<JWTUserData> validate(String token) {
        try {
            var decoded = JWT.require(algorithm()).withIssuer(issuer).build().verify(token);
            return Optional.of(new JWTUserData(UUID.fromString(decoded.getClaim("userId").asString()), decoded.getSubject()));
        } catch (JWTVerificationException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Instant expiration() {
        return LocalDateTime.now().plusSeconds(expirationSeconds).toInstant(ZoneOffset.of("-03:00"));
    }
}

package me.gabrielporto.comandapro.infrastructure.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import me.gabrielporto.comandapro.core.domain.user.User;
import me.gabrielporto.comandapro.infrastructure.security.TokenConfig;
import me.gabrielporto.comandapro.infrastructure.web.dto.request.LoginRequest;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.ApiResponse;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.AuthResponse;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.UserResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    public AuthController(AuthenticationManager authenticationManager, TokenConfig tokenConfig) {
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
        );

        Authentication authentication = authenticationManager.authenticate(authToken);
        User user = (User) authentication.getPrincipal();

        String token = tokenConfig.generateToken(user);

        AuthResponse authResponse = new AuthResponse(
                token,
                new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getTel(),
                        user.getRole().name(),
                        user.getStatus().name(),
                        user.getCreatedAt()
                )
        );

        return ResponseEntity.ok(new ApiResponse(
                "Login realizado com sucesso",
                authResponse
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> me(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(new ApiResponse(
                "Usuário autenticado",
                new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getTel(),
                        user.getRole().name(),
                        user.getStatus().name(),
                        user.getCreatedAt()
                )
        ));
    }

}

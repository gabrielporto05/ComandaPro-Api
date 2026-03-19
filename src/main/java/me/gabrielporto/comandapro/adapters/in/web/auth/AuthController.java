package me.gabrielporto.comandapro.adapters.in.web.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import me.gabrielporto.comandapro.adapters.in.web.dto.ApiResponse;
import me.gabrielporto.comandapro.adapters.in.web.dto.AuthResponse;
import me.gabrielporto.comandapro.adapters.in.web.dto.LoginRequest;
import me.gabrielporto.comandapro.adapters.in.web.dto.RegisterRequest;
import me.gabrielporto.comandapro.config.TokenService;
import me.gabrielporto.comandapro.core.domain.user.Role;
import me.gabrielporto.comandapro.core.domain.user.User;
import me.gabrielporto.comandapro.core.domain.user.UserStatus;
import me.gabrielporto.comandapro.core.ports.out.UserRepositoryPort;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
            TokenService tokenService,
            UserRepositoryPort userRepository,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email()).orElseThrow();
        String token = tokenService.generate(user);

        return ResponseEntity.ok(new ApiResponse<>("Login realizado com sucesso", AuthResponse.of(token, user)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        User user = new User(request.name(), request.email(), request.tel(), passwordEncoder.encode(request.password()));
        user.setRole(Role.OWNER);
        user.setStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);
        String token = tokenService.generate(user);
        return ResponseEntity.ok(new ApiResponse<>("Registro realizado com sucesso", AuthResponse.of(token, user)));
    }
}

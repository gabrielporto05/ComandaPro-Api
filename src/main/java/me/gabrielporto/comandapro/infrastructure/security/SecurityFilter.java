package me.gabrielporto.comandapro.infrastructure.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.gabrielporto.comandapro.core.domain.user.User;
import me.gabrielporto.comandapro.infrastructure.persistence.repository.UserJpaRepository;

@Slf4j
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenConfig tokenConfig;
    private final UserJpaRepository userRepository;

    public SecurityFilter(TokenConfig tokenConfig, UserJpaRepository userRepository) {
        this.tokenConfig = tokenConfig;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws IOException, ServletException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        log.info("🔍 Request: {} {}", method, path);

        if (method.equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isPublicPath(path)) {
            log.info("🔓 Rota pública detectada: {} - permitindo acesso sem autenticação", path);
            filterChain.doFilter(request, response);
            return;
        }

        Optional<String> token = extractToken(request);

        if (token.isPresent()) {
            log.info("🔑 Token encontrado, validando...");
            authenticateUser(token.get());
        } else {
            log.info("🔓 Sem token - se a rota for privada, o Spring Security vai bloquear");
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {

        return path.startsWith("/uploads/");

    }

    private Optional<String> extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.info("🎫 Token extraído: {}...", token.substring(0, Math.min(10, token.length())));
            return Optional.of(token);
        }

        log.info("🔓 Nenhum token encontrado no header Authorization");
        return Optional.empty();
    }

    private void authenticateUser(String token) {
        log.info("🔐 Validando token...");

        Optional<JWTUserData> jwtData = tokenConfig.validateToken(token);

        if (jwtData.isPresent()) {
            log.info("✅ Token válido para usuário: {}", jwtData.get().email());

            Optional<User> user = userRepository.findById(jwtData.get().id());

            if (user.isPresent()) {
                log.info("👤 Usuário encontrado no banco: {}", user.get().getEmail());

                UsernamePasswordAuthenticationToken authentication
                        = new UsernamePasswordAuthenticationToken(
                                user.get(),
                                null,
                                user.get().getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("✅ Autenticação configurada no SecurityContext");
            } else {
                log.warn("⚠️ Usuário não encontrado no banco para ID: {}", jwtData.get().id());
            }
        } else {
            log.warn("❌ Token inválido ou expirado");
        }
    }
}

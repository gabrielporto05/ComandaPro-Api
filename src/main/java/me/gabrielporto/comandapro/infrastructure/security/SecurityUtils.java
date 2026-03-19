package me.gabrielporto.comandapro.infrastructure.security;

import org.springframework.security.core.context.SecurityContextHolder;

import me.gabrielporto.comandapro.core.domain.user.User;

public class SecurityUtils {

    public static User getAuthenticatedUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}

package me.gabrielporto.comandapro.core.domain.user;

public enum UserRole {
    OWNER,
    ADMIN;

    public boolean isAdmin() {
        return this == ADMIN;
    }
}

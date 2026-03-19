package me.gabrielporto.comandapro.core.domain.user;

public enum UserStatus {
    ACTIVE,
    INACTIVE,
    BLOCKED;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isBlocked() {
        return this == BLOCKED;
    }
}

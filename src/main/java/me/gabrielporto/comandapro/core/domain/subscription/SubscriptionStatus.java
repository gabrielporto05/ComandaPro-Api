package me.gabrielporto.comandapro.core.domain.subscription;

public enum SubscriptionStatus {
    ACTIVE,
    SUSPENDED,
    CANCELED,
    EXPIRED;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isSuspended() {
        return this == SUSPENDED;
    }

    public boolean isCanceled() {
        return this == CANCELED;
    }

    public boolean isExpired() {
        return this == EXPIRED;
    }
}

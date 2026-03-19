package me.gabrielporto.comandapro.core.domain.store;

public enum StoreStatus {
    ACTIVE,
    SUSPENDED,
    CANCELED;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isSuspended() {
        return this == SUSPENDED;
    }

    public boolean isCanceled() {
        return this == CANCELED;
    }
}

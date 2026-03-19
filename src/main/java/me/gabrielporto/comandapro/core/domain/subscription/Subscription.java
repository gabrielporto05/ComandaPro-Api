package me.gabrielporto.comandapro.core.domain.subscription;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import me.gabrielporto.comandapro.core.domain.order.PaymentMethod;
import me.gabrielporto.comandapro.core.domain.store.Store;

public class Subscription {

    private UUID id = UUID.randomUUID();
    private Store store;
    private String plan;
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lastPaymentDate;
    private LocalDate nextPaymentDate;
    private PaymentMethod paymentMethod;
    private String paymentId;
    private OffsetDateTime createdAt = OffsetDateTime.now();
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public Subscription() {
    }

    public Subscription(Store store, String plan) {
        this.store = store;
        this.plan = plan;
    }

    public void suspend() {
        this.status = SubscriptionStatus.SUSPENDED;
        touch();
    }

    public void cancel() {
        this.status = SubscriptionStatus.CANCELED;
        touch();
    }

    public void expire() {
        this.status = SubscriptionStatus.EXPIRED;
        touch();
    }

    public void activate() {
        this.status = SubscriptionStatus.ACTIVE;
        touch();
    }

    public void setStore(Store store) {
        this.store = store;
    }

    private void touch() {
        this.updatedAt = OffsetDateTime.now();
    }
}

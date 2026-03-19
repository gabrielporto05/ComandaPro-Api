package me.gabrielporto.comandapro.core.domain.checkout;

import java.time.OffsetDateTime;
import java.util.UUID;
import me.gabrielporto.comandapro.core.domain.subscription.PlanType;

public class CheckoutTemporary {

    private UUID id = UUID.randomUUID();
    private PlanType plan;
    private OffsetDateTime expiresAt;
    private String customerName;
    private String customerEmail;
    private String customerTel;
    private String customerPasswordHash;
    private String storeName;
    private String storeSlug;
    private boolean processed = false;
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public CheckoutTemporary() {
    }

    public CheckoutTemporary(PlanType plan, OffsetDateTime expiresAt, String customerName, String customerEmail,
                             String customerTel, String customerPasswordHash, String storeName, String storeSlug) {
        this.plan = plan;
        this.expiresAt = expiresAt;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerTel = customerTel;
        this.customerPasswordHash = customerPasswordHash;
        this.storeName = storeName;
        this.storeSlug = storeSlug;
    }

    public void markProcessed() {
        this.processed = true;
    }
}

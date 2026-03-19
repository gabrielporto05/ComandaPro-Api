package me.gabrielporto.comandapro.adapters.out.persistence.entities;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import me.gabrielporto.comandapro.core.domain.subscription.PlanType;

@Entity
@Table(name = "checkout_temporary")
public class CheckoutTemporaryEntity {

    @Id
    @Column(nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private PlanType plan;

    @Column(nullable = false)
    private OffsetDateTime expiresAt;

    @Column(nullable = false)
    private String customerName;
    @Column(nullable = false)
    private String customerEmail;
    @Column(nullable = false)
    private String customerTel;
    @Column(nullable = false)
    private String customerPasswordHash;

    @Column(nullable = false)
    private String storeName;
    @Column(nullable = false)
    private String storeSlug;

    @Column(nullable = false)
    private boolean processed;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    // getters/setters
}

package me.gabrielporto.comandapro.adapters.out.persistence.entities;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import me.gabrielporto.comandapro.core.domain.order.PaymentMethod;
import me.gabrielporto.comandapro.core.domain.subscription.SubscriptionStatus;

@Entity
@Table(name = "subscriptions")
public class SubscriptionEntity {

    @Id
    @Column(nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "store_id", unique = true)
    private StoreEntity store;

    @Column(nullable = false)
    private String plan;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lastPaymentDate;
    private LocalDate nextPaymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    private String paymentId;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    // getters/setters
}

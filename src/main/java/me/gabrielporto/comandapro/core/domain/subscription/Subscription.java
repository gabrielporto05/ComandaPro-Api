package me.gabrielporto.comandapro.core.domain.subscription;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.gabrielporto.comandapro.core.domain.store.Store;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false, unique = true, columnDefinition = "UUID NOT NULL UNIQUE")
    private Store store;

    @Column(name = "plan", nullable = false, columnDefinition = "VARCHAR(255) NOT NULL")
    private String plan;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(50) NOT NULL")
    private SubscriptionStatus status;

    @Column(name = "start_date", nullable = false, columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false, columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime endDate;

    @Column(name = "last_payment_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime lastPaymentDate;

    @Column(name = "next_payment_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime nextPaymentDate;

    @Column(name = "payment_method", columnDefinition = "VARCHAR(100)")
    private String paymentMethod;

    @Column(name = "payment_id", columnDefinition = "VARCHAR(255)")
    private String paymentId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = SubscriptionStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

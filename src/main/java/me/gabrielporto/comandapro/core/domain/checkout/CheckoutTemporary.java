package me.gabrielporto.comandapro.core.domain.checkout;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "checkout_temporary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutTemporary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "plan", nullable = false, columnDefinition = "VARCHAR(50) NOT NULL")
    private String plan;

    @Column(name = "expires_at", nullable = false, columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime expiresAt;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "client_name", columnDefinition = "VARCHAR(255) NOT NULL")),
        @AttributeOverride(name = "email", column = @Column(name = "client_email", columnDefinition = "VARCHAR(255) NOT NULL")),
        @AttributeOverride(name = "tel", column = @Column(name = "client_tel", columnDefinition = "VARCHAR(20) NOT NULL")),
        @AttributeOverride(name = "passwordHash", column = @Column(name = "client_password_hash", columnDefinition = "VARCHAR(255) NOT NULL"))
    })
    private ClientData client;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "store_name", columnDefinition = "VARCHAR(255) NOT NULL")),
        @AttributeOverride(name = "slug", column = @Column(name = "store_slug", columnDefinition = "VARCHAR(255) NOT NULL"))
    })
    private StoreData store;

    @Column(name = "processed", nullable = false, columnDefinition = "BOOLEAN NOT NULL DEFAULT FALSE")
    private boolean processed;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime createdAt;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClientData {

        private String name;
        private String email;
        private String tel;
        private String passwordHash;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StoreData {

        private String name;
        private String slug;
    }
}

package me.gabrielporto.comandapro.core.domain.store;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false, columnDefinition = "UUID NOT NULL")
    private Store store;

    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @Column(name = "description", length = 500, columnDefinition = "VARCHAR(500)")
    private String description;

    @Column(name = "price", nullable = false, columnDefinition = "DECIMAL(10,2) NOT NULL")
    private Double price;

    @Column(name = "photo_url", columnDefinition = "VARCHAR(255)")
    private String photoUrl;

    @Column(name = "category", columnDefinition = "VARCHAR(100)")
    private String category;

    @Column(name = "is_available", nullable = false, columnDefinition = "BOOLEAN NOT NULL DEFAULT TRUE")
    private Boolean isAvailable;

    @Column(name = "is_highlight", nullable = false, columnDefinition = "BOOLEAN NOT NULL DEFAULT FALSE")
    private Boolean isHighlight;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.isAvailable == null) {
            this.isAvailable = true;
        }
        if (this.isHighlight == null) {
            this.isHighlight = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

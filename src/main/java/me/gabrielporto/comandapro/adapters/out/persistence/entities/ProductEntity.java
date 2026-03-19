package me.gabrielporto.comandapro.adapters.out.persistence.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @Column(nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "store_id")
    private StoreEntity store;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private String photoUrl;
    private String category;
    private boolean isAvailable;
    private boolean isHighlight;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // getters/setters
}

package me.gabrielporto.comandapro.core.domain.store;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Produto no domínio (sem anotações JPA).
 */
public class Product {

    private UUID id = UUID.randomUUID();
    private Store store;
    private String name;
    private String description;
    private BigDecimal price;
    private String photoUrl;
    private String category;
    private boolean isAvailable = true;
    private boolean isHighlight = false;
    private OffsetDateTime createdAt = OffsetDateTime.now();
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public Product() {
    }

    public Product(Store store, String name, BigDecimal price) {
        this.store = store;
        this.name = name;
        this.price = price;
    }

    public void markAvailable() {
        this.isAvailable = true;
        touch();
    }

    public void markUnavailable() {
        this.isAvailable = false;
        touch();
    }

    public void highlight() {
        this.isHighlight = true;
        touch();
    }

    public void unhighlight() {
        this.isHighlight = false;
        touch();
    }

    private void touch() {
        this.updatedAt = OffsetDateTime.now();
    }

    // getters/setters essenciais
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}

package me.gabrielporto.comandapro.adapters.out.persistence.entities;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import me.gabrielporto.comandapro.core.domain.order.PaymentMethod;
import me.gabrielporto.comandapro.core.domain.store.StoreStatus;
import lombok.Data;

@Data
@Entity
@Table(name = "stores")
public class StoreEntity {

    @Id
    @Column(nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    private String email;
    private String tel;
    private String description;
    private String address;

    @ElementCollection
    @CollectionTable(name = "store_photos", joinColumns = @JoinColumn(name = "store_id"))
    @Column(name = "photo_url")
    private List<String> photos = new ArrayList<>();

    private BigDecimal feeDelivery;

    @ElementCollection
    @CollectionTable(name = "store_categories", joinColumns = @JoinColumn(name = "store_id"))
    @Column(name = "category")
    private List<String> categories = new ArrayList<>();

    @Convert(converter = PaymentMethodArrayConverter.class)
    @Column(name = "payment_methods")
    private PaymentMethod[] paymentMethods;

    @Enumerated(EnumType.STRING)
    private StoreStatus status;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductEntity> products = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreHoursEntity> hours = new ArrayList<>();

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    // getters/setters
}

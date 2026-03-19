package me.gabrielporto.comandapro.adapters.out.persistence.entities;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import me.gabrielporto.comandapro.core.domain.order.OrderStatus;
import me.gabrielporto.comandapro.core.domain.order.PaymentMethod;
import lombok.Data;

@Data
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @Column(nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "store_id")
    private StoreEntity store;

    @Column(nullable = false)
    private String orderCode;

    @Column(nullable = false)
    private String customerName;
    @Column(nullable = false)
    private String customerPhone;
    private String customerAddress;
    private String customerNumber;
    private String customerComplement;
    private String customerNeighborhood;
    private String customerCity;
    private String customerZipcode;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private BigDecimal changeFor;
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee;
    private String generalObservation;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Integer preparationTimeMinutes;
    private OffsetDateTime acceptedAt;
    private OffsetDateTime readyAt;
    private OffsetDateTime deliveredAt;
    private OffsetDateTime cancelledAt;
    private String cancellationReason;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items = new ArrayList<>();

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    // getters/setters
}

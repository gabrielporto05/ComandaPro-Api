package me.gabrielporto.comandapro.core.domain.order;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.gabrielporto.comandapro.core.domain.store.Store;

public class Order {

    private UUID id = UUID.randomUUID();
    private Store store;
    private String orderCode;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private String customerNumber;
    private String customerComplement;
    private String customerNeighborhood;
    private String customerCity;
    private String customerZipcode;
    private PaymentMethod paymentMethod;
    private BigDecimal changeFor;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private BigDecimal deliveryFee;
    private String generalObservation;
    private OrderStatus status = OrderStatus.PENDING;
    private Integer preparationTimeMinutes;
    private OffsetDateTime acceptedAt;
    private OffsetDateTime readyAt;
    private OffsetDateTime deliveredAt;
    private OffsetDateTime cancelledAt;
    private String cancellationReason;
    private List<OrderItem> items = new ArrayList<>();
    private OffsetDateTime createdAt = OffsetDateTime.now();
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public Order() {
    }

    public Order(Store store, String orderCode) {
        this.store = store;
        this.orderCode = orderCode;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        calculateTotal();
    }

    public void calculateTotal() {
        BigDecimal itemsTotal = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal fee = deliveryFee != null ? deliveryFee : BigDecimal.ZERO;
        this.totalAmount = itemsTotal.add(fee);
        touch();
    }

    public void accept(Integer preparationMinutes) {
        this.status = OrderStatus.PREPARING;
        this.preparationTimeMinutes = preparationMinutes;
        this.acceptedAt = OffsetDateTime.now();
        touch();
    }

    public void markAsReady() {
        this.status = OrderStatus.READY;
        this.readyAt = OffsetDateTime.now();
        touch();
    }

    public void markAsDelivered() {
        this.status = OrderStatus.DELIVERED;
        this.deliveredAt = OffsetDateTime.now();
        touch();
    }

    public void cancel(String reason) {
        this.status = OrderStatus.CANCELLED;
        this.cancellationReason = reason;
        this.cancelledAt = OffsetDateTime.now();
        touch();
    }

    private void touch() {
        this.updatedAt = OffsetDateTime.now();
    }

    // getters and setters omitted for brevity
}

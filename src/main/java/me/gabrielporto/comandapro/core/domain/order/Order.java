package me.gabrielporto.comandapro.core.domain.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.gabrielporto.comandapro.core.domain.store.Store;
import me.gabrielporto.comandapro.shared.exception.BusinessException;

@Entity
@Table(name = "orders", uniqueConstraints = {
    @jakarta.persistence.UniqueConstraint(name = "uk_order_store_code", columnNames = {"store_id", "order_code"})
})
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "order_code", nullable = false, length = 6)
    private String orderCode;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_phone", nullable = false)
    private String customerPhone;

    @Column(name = "customer_address", nullable = false)
    private String customerAddress;

    @Column(name = "customer_address_number")
    private String customerAddressNumber;

    @Column(name = "customer_address_complement")
    private String customerAddressComplement;

    @Column(name = "customer_address_neighborhood")
    private String customerAddressNeighborhood;

    @Column(name = "customer_address_city")
    private String customerAddressCity;

    @Column(name = "customer_address_zipcode")
    private String customerAddressZipcode;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "change_for")
    private Double changeFor;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "delivery_fee")
    private Double deliveryFee;

    @Column(name = "general_observation", length = 500)
    private String generalObservation;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "preparation_time_minutes")
    private Integer preparationTimeMinutes;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "ready_at")
    private LocalDateTime readyAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = OrderStatus.PENDING;
        }
        if (this.totalAmount == null) {
            this.totalAmount = 0.0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        calculateTotal();
    }

    public void calculateTotal() {
        double subtotal = items.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
        double fee = deliveryFee == null ? 0.0 : deliveryFee.doubleValue();
        this.totalAmount = subtotal + fee;
    }

    public void accept() {
        if (this.status != OrderStatus.PENDING) {
            throw new BusinessException("Apenas pedidos pendentes podem ser aceitos");
        }
        this.status = OrderStatus.PREPARING;
        this.acceptedAt = LocalDateTime.now();
    }

    public void markAsReady() {
        if (this.status != OrderStatus.PREPARING) {
            throw new BusinessException("Apenas pedidos em preparação podem ser marcados como prontos");
        }
        this.status = OrderStatus.READY;
        this.readyAt = LocalDateTime.now();
    }

    public void markAsDelivered() {
        if (this.status != OrderStatus.READY) {
            throw new BusinessException("Apenas pedidos prontos podem ser marcados como entregues");
        }
        this.status = OrderStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    public void cancel(String reason) {
        if (this.status == OrderStatus.DELIVERED || this.status == OrderStatus.CANCELLED) {
            throw new BusinessException("Pedido já foi entregue ou cancelado");
        }
        this.status = OrderStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
    }

    // Getters e setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerAddressNumber() {
        return customerAddressNumber;
    }

    public void setCustomerAddressNumber(String customerAddressNumber) {
        this.customerAddressNumber = customerAddressNumber;
    }

    public String getCustomerAddressComplement() {
        return customerAddressComplement;
    }

    public void setCustomerAddressComplement(String customerAddressComplement) {
        this.customerAddressComplement = customerAddressComplement;
    }

    public String getCustomerAddressNeighborhood() {
        return customerAddressNeighborhood;
    }

    public void setCustomerAddressNeighborhood(String customerAddressNeighborhood) {
        this.customerAddressNeighborhood = customerAddressNeighborhood;
    }

    public String getCustomerAddressCity() {
        return customerAddressCity;
    }

    public void setCustomerAddressCity(String customerAddressCity) {
        this.customerAddressCity = customerAddressCity;
    }

    public String getCustomerAddressZipcode() {
        return customerAddressZipcode;
    }

    public void setCustomerAddressZipcode(String customerAddressZipcode) {
        this.customerAddressZipcode = customerAddressZipcode;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getChangeFor() {
        return changeFor;
    }

    public void setChangeFor(Double changeFor) {
        this.changeFor = changeFor;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getGeneralObservation() {
        return generalObservation;
    }

    public void setGeneralObservation(String generalObservation) {
        this.generalObservation = generalObservation;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Integer getPreparationTimeMinutes() {
        return preparationTimeMinutes;
    }

    public void setPreparationTimeMinutes(Integer preparationTimeMinutes) {
        this.preparationTimeMinutes = preparationTimeMinutes;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public LocalDateTime getReadyAt() {
        return readyAt;
    }

    public void setReadyAt(LocalDateTime readyAt) {
        this.readyAt = readyAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public static OrderBuilder builder() {
        return new OrderBuilder();
    }

    public static final class OrderBuilder {

        private UUID id;
        private Store store;
        private String orderCode;
        private String customerName;
        private String customerPhone;
        private String customerAddress;
        private String customerAddressNumber;
        private String customerAddressComplement;
        private String customerAddressNeighborhood;
        private String customerAddressCity;
        private String customerAddressZipcode;
        private PaymentMethod paymentMethod;
        private Double changeFor;
        private Double totalAmount;
        private Double deliveryFee;
        private String generalObservation;
        private OrderStatus status;
        private Integer preparationTimeMinutes;
        private LocalDateTime acceptedAt;
        private LocalDateTime readyAt;
        private LocalDateTime deliveredAt;
        private LocalDateTime cancelledAt;
        private String cancellationReason;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<OrderItem> items = new ArrayList<>();

        private OrderBuilder() {
        }

        public OrderBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public OrderBuilder store(Store store) {
            this.store = store;
            return this;
        }

        public OrderBuilder orderCode(String orderCode) {
            this.orderCode = orderCode;
            return this;
        }

        public OrderBuilder customerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public OrderBuilder customerPhone(String customerPhone) {
            this.customerPhone = customerPhone;
            return this;
        }

        public OrderBuilder customerAddress(String customerAddress) {
            this.customerAddress = customerAddress;
            return this;
        }

        public OrderBuilder customerAddressNumber(String customerAddressNumber) {
            this.customerAddressNumber = customerAddressNumber;
            return this;
        }

        public OrderBuilder customerAddressComplement(String customerAddressComplement) {
            this.customerAddressComplement = customerAddressComplement;
            return this;
        }

        public OrderBuilder customerAddressNeighborhood(String customerAddressNeighborhood) {
            this.customerAddressNeighborhood = customerAddressNeighborhood;
            return this;
        }

        public OrderBuilder customerAddressCity(String customerAddressCity) {
            this.customerAddressCity = customerAddressCity;
            return this;
        }

        public OrderBuilder customerAddressZipcode(String customerAddressZipcode) {
            this.customerAddressZipcode = customerAddressZipcode;
            return this;
        }

        public OrderBuilder paymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public OrderBuilder changeFor(Double changeFor) {
            this.changeFor = changeFor;
            return this;
        }

        public OrderBuilder totalAmount(Double totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public OrderBuilder deliveryFee(Double deliveryFee) {
            this.deliveryFee = deliveryFee;
            return this;
        }

        public OrderBuilder generalObservation(String generalObservation) {
            this.generalObservation = generalObservation;
            return this;
        }

        public OrderBuilder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public OrderBuilder preparationTimeMinutes(Integer preparationTimeMinutes) {
            this.preparationTimeMinutes = preparationTimeMinutes;
            return this;
        }

        public OrderBuilder acceptedAt(LocalDateTime acceptedAt) {
            this.acceptedAt = acceptedAt;
            return this;
        }

        public OrderBuilder readyAt(LocalDateTime readyAt) {
            this.readyAt = readyAt;
            return this;
        }

        public OrderBuilder deliveredAt(LocalDateTime deliveredAt) {
            this.deliveredAt = deliveredAt;
            return this;
        }

        public OrderBuilder cancelledAt(LocalDateTime cancelledAt) {
            this.cancelledAt = cancelledAt;
            return this;
        }

        public OrderBuilder cancellationReason(String cancellationReason) {
            this.cancellationReason = cancellationReason;
            return this;
        }

        public OrderBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public OrderBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public OrderBuilder items(List<OrderItem> items) {
            this.items = items != null ? items : new ArrayList<>();
            return this;
        }

        public Order build() {
            Order order = new Order();
            order.setId(id);
            order.setStore(store);
            order.setOrderCode(orderCode);
            order.setCustomerName(customerName);
            order.setCustomerPhone(customerPhone);
            order.setCustomerAddress(customerAddress);
            order.setCustomerAddressNumber(customerAddressNumber);
            order.setCustomerAddressComplement(customerAddressComplement);
            order.setCustomerAddressNeighborhood(customerAddressNeighborhood);
            order.setCustomerAddressCity(customerAddressCity);
            order.setCustomerAddressZipcode(customerAddressZipcode);
            order.setPaymentMethod(paymentMethod);
            order.setChangeFor(changeFor);
            order.setTotalAmount(totalAmount);
            order.setDeliveryFee(deliveryFee);
            order.setGeneralObservation(generalObservation);
            order.setStatus(status);
            order.setPreparationTimeMinutes(preparationTimeMinutes);
            order.setAcceptedAt(acceptedAt);
            order.setReadyAt(readyAt);
            order.setDeliveredAt(deliveredAt);
            order.setCancelledAt(cancelledAt);
            order.setCancellationReason(cancellationReason);
            order.setCreatedAt(createdAt);
            order.setUpdatedAt(updatedAt);
            order.setItems(items);
            return order;
        }
    }
}

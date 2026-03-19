package me.gabrielporto.comandapro.core.domain.store;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.gabrielporto.comandapro.core.domain.order.Order;
import me.gabrielporto.comandapro.core.domain.order.PaymentMethod;
import me.gabrielporto.comandapro.core.domain.subscription.Subscription;
import me.gabrielporto.comandapro.core.domain.user.User;

/**
 * Domínio puro da loja, sem dependência de JPA.
 */
public class Store {

    private UUID id = UUID.randomUUID();
    private String name;
    private String slug;
    private User owner;
    private String email;
    private String tel;
    private String description;
    private String address;
    private List<String> photos = new ArrayList<>();
    private BigDecimal feeDelivery;
    private List<String> categories = new ArrayList<>();
    private PaymentMethod[] paymentMethods = new PaymentMethod[0];
    private StoreStatus status = StoreStatus.ACTIVE;
    private List<Product> products = new ArrayList<>();
    private List<StoreHours> hours = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();
    private Subscription subscription;
    private OffsetDateTime createdAt = OffsetDateTime.now();
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public Store() {
    }

    public Store(String name, String slug, User owner) {
        this.name = name;
        this.slug = slug;
        this.owner = owner;
    }

    public void activate() {
        this.status = StoreStatus.ACTIVE;
        touch();
    }

    public void suspend() {
        this.status = StoreStatus.SUSPENDED;
        touch();
    }

    public void cancel() {
        this.status = StoreStatus.CANCELED;
        touch();
    }

    public void addProduct(Product product) {
        products.add(product);
        product.setStore(this);
    }

    public void addHour(StoreHours storeHours) {
        hours.add(storeHours);
        storeHours.setStore(this);
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
        if (subscription != null) {
            subscription.setStore(this);
        }
    }

    private void touch() {
        this.updatedAt = OffsetDateTime.now();
    }

    // getters and setters omitidos para brevidade
}

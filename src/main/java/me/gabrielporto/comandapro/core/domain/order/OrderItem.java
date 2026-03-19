package me.gabrielporto.comandapro.core.domain.order;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import me.gabrielporto.comandapro.core.domain.store.Product;

public class OrderItem {

    private UUID id = UUID.randomUUID();
    private Order order;
    private Product product;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String observation;
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public OrderItem() {}

    public OrderItem(Order order, Product product, Integer quantity, BigDecimal unitPrice) {
        this.order = order;
        this.product = product;
        this.productName = product.getName();
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}

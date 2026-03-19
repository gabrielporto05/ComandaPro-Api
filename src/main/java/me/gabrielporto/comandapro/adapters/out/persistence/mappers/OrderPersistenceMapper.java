package me.gabrielporto.comandapro.adapters.out.persistence.mappers;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

import me.gabrielporto.comandapro.adapters.out.persistence.entities.OrderEntity;
import me.gabrielporto.comandapro.adapters.out.persistence.entities.OrderItemEntity;
import me.gabrielporto.comandapro.adapters.out.persistence.entities.ProductEntity;
import me.gabrielporto.comandapro.adapters.out.persistence.entities.StoreEntity;
import me.gabrielporto.comandapro.core.domain.order.Order;
import me.gabrielporto.comandapro.core.domain.order.OrderItem;

public class OrderPersistenceMapper {

    public static OrderEntity toEntity(Order order, StoreEntity storeEntity) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setStore(storeEntity);
        entity.setOrderCode(order.getOrderCode());
        entity.setCustomerName(order.getCustomerName());
        entity.setCustomerPhone(order.getCustomerPhone());
        entity.setCustomerAddress(order.getCustomerAddress());
        entity.setCustomerNumber(order.getCustomerNumber());
        entity.setCustomerComplement(order.getCustomerComplement());
        entity.setCustomerNeighborhood(order.getCustomerNeighborhood());
        entity.setCustomerCity(order.getCustomerCity());
        entity.setCustomerZipcode(order.getCustomerZipcode());
        entity.setPaymentMethod(order.getPaymentMethod());
        entity.setChangeFor(order.getChangeFor());
        entity.setTotalAmount(order.getTotalAmount());
        entity.setDeliveryFee(order.getDeliveryFee());
        entity.setGeneralObservation(order.getGeneralObservation());
        entity.setStatus(order.getStatus());
        entity.setPreparationTimeMinutes(order.getPreparationTimeMinutes());
        entity.setAcceptedAt(order.getAcceptedAt());
        entity.setReadyAt(order.getReadyAt());
        entity.setDeliveredAt(order.getDeliveredAt());
        entity.setCancelledAt(order.getCancelledAt());
        entity.setCancellationReason(order.getCancellationReason());
        entity.setCreatedAt(order.getCreatedAt());
        entity.setUpdatedAt(order.getUpdatedAt());
        if (order.getItems() != null) {
            entity.setItems(order.getItems().stream()
                    .map(item -> toItemEntity(item, entity))
                    .collect(Collectors.toList()));
        }
        return entity;
    }

    public static OrderItemEntity toItemEntity(OrderItem item, OrderEntity order) {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(item.getId());
        entity.setOrder(order);
        if (item.getProduct() != null) {
            ProductEntity p = new ProductEntity();
            p.setId(item.getProduct().getId());
            entity.setProduct(p);
        }
        entity.setProductName(item.getProductName());
        entity.setQuantity(item.getQuantity());
        entity.setUnitPrice(item.getUnitPrice());
        entity.setObservation(item.getObservation());
        entity.setCreatedAt(item.getCreatedAt());
        return entity;
    }

    public static Order toDomain(OrderEntity entity) {
        Order order = new Order();
        order.setId(entity.getId());
        order.setStore(StorePersistenceMapper.toDomain(entity.getStore()));
        order.setOrderCode(entity.getOrderCode());
        order.setCustomerName(entity.getCustomerName());
        order.setCustomerPhone(entity.getCustomerPhone());
        order.setCustomerAddress(entity.getCustomerAddress());
        order.setCustomerNumber(entity.getCustomerNumber());
        order.setCustomerComplement(entity.getCustomerComplement());
        order.setCustomerNeighborhood(entity.getCustomerNeighborhood());
        order.setCustomerCity(entity.getCustomerCity());
        order.setCustomerZipcode(entity.getCustomerZipcode());
        order.setPaymentMethod(entity.getPaymentMethod());
        order.setChangeFor(entity.getChangeFor());
        order.setDeliveryFee(entity.getDeliveryFee());
        order.setGeneralObservation(entity.getGeneralObservation());
        order.setStatus(entity.getStatus());
        order.setAcceptedAt(entity.getAcceptedAt());
        order.setReadyAt(entity.getReadyAt());
        order.setDeliveredAt(entity.getDeliveredAt());
        order.setCancelledAt(entity.getCancelledAt());
        order.setCancellationReason(entity.getCancellationReason());
        order.setCreatedAt(entity.getCreatedAt());
        order.setUpdatedAt(entity.getUpdatedAt());
        var items = entity.getItems() != null
                ? entity.getItems().stream().map(OrderPersistenceMapper::toDomainItem).toList()
                : new ArrayList<OrderItem>();
        order.setItems(items);
        order.calculateTotal();
        return order;
    }

    private static OrderItem toDomainItem(OrderItemEntity entity) {
        OrderItem item = new OrderItem();
        item.setOrder(null);
        item.setProductName(entity.getProductName());
        item.setQuantity(entity.getQuantity());
        item.setUnitPrice(entity.getUnitPrice());
        item.setObservation(entity.getObservation());
        return item;
    }
}

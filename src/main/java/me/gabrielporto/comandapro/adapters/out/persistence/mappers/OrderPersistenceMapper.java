package me.gabrielporto.comandapro.adapters.out.persistence.mappers;

import me.gabrielporto.comandapro.adapters.out.persistence.entities.OrderEntity;
import me.gabrielporto.comandapro.core.domain.order.Order;

public class OrderPersistenceMapper {
    public static OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        return entity;
    }

    public static Order toDomain(OrderEntity entity) {
        return new Order();
    }
}

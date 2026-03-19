package me.gabrielporto.comandapro.core.ports.out;

import java.util.Optional;
import java.util.UUID;

import me.gabrielporto.comandapro.core.domain.order.Order;

public interface OrderRepositoryPort {

    Order save(Order order);

    Optional<Order> findById(UUID id);

    Optional<Order> findByOrderCodeAndStoreId(String code, UUID storeId);
}

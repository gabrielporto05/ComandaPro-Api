package me.gabrielporto.comandapro.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import me.gabrielporto.comandapro.adapters.out.persistence.entities.OrderEntity;
import me.gabrielporto.comandapro.adapters.out.persistence.mappers.OrderPersistenceMapper;
import me.gabrielporto.comandapro.adapters.out.persistence.repositories.OrderJpaRepository;
import me.gabrielporto.comandapro.core.domain.order.Order;
import me.gabrielporto.comandapro.core.domain.order.OrderStatus;
import me.gabrielporto.comandapro.core.ports.out.OrderRepositoryPort;

@Component
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository repository;

    public OrderRepositoryAdapter(OrderJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = repository.save(OrderPersistenceMapper.toEntity(order, null));
        return OrderPersistenceMapper.toDomain(entity);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return repository.findById(id).map(OrderPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Order> findByOrderCodeAndStoreId(String code, UUID storeId) {
        return repository.findByOrderCodeAndStoreId(code, storeId).map(OrderPersistenceMapper::toDomain);
    }

    public Page<OrderEntity> findByStore(UUID storeId, Pageable pageable) {
        return repository.findByStoreId(storeId, pageable);
    }

    public Page<OrderEntity> findByStoreAndStatus(UUID storeId, OrderStatus status, Pageable pageable) {
        return repository.findByStoreIdAndStatus(storeId, status, pageable);
    }
}

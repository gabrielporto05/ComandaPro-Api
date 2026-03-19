package me.gabrielporto.comandapro.adapters.out.persistence.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import me.gabrielporto.comandapro.adapters.out.persistence.entities.OrderEntity;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

    Optional<OrderEntity> findByOrderCodeAndStoreId(String orderCode, UUID storeId);
}

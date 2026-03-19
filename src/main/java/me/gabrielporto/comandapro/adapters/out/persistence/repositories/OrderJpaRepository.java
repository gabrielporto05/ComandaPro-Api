package me.gabrielporto.comandapro.adapters.out.persistence.repositories;

import java.util.Optional;
import java.util.UUID;
import me.gabrielporto.comandapro.adapters.out.persistence.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
    Optional<OrderEntity> findByOrderCodeAndStoreId(String orderCode, UUID storeId);
}

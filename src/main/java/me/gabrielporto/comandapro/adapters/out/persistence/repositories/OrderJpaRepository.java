package me.gabrielporto.comandapro.adapters.out.persistence.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import me.gabrielporto.comandapro.adapters.out.persistence.entities.OrderEntity;
import me.gabrielporto.comandapro.core.domain.order.OrderStatus;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

    Optional<OrderEntity> findByOrderCodeAndStoreId(String orderCode, UUID storeId);

    Page<OrderEntity> findByStoreId(UUID storeId, Pageable pageable);

    Page<OrderEntity> findByStoreIdAndStatus(UUID storeId, OrderStatus status, Pageable pageable);

    @Query("select o from OrderEntity o where o.store.id = :storeId and date(o.createdAt) = :date")
    List<OrderEntity> findByStoreIdAndCreatedAtDate(UUID storeId, LocalDate date);

    List<OrderEntity> findByStoreIdAndCreatedAtBetween(UUID storeId, LocalDateTime start, LocalDateTime end);

    List<OrderEntity> findByStoreIdOrderByCreatedAtDesc(UUID storeId, Pageable pageable);
}

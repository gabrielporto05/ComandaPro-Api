package me.gabrielporto.comandapro.infrastructure.persistence.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import me.gabrielporto.comandapro.core.domain.order.Order;
import me.gabrielporto.comandapro.core.domain.order.OrderStatus;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, UUID> {

    Page<Order> findByStoreId(UUID storeId, Pageable pageable);

    Page<Order> findByStoreIdAndStatus(UUID storeId, OrderStatus status, Pageable pageable);

    List<Order> findByStoreIdOrderByCreatedAtDesc(UUID storeId, Pageable pageable);

    List<Order> findByStoreIdAndCustomerPhoneContaining(UUID storeId, String phone);

    Optional<Order> findByStoreIdAndOrderCode(UUID storeId, String orderCode);

    boolean existsByStoreIdAndOrderCode(UUID storeId, String orderCode);

    @Query("SELECT o FROM Order o WHERE o.store.id = :storeId AND DATE(o.createdAt) = :date")
    List<Order> findByStoreIdAndCreatedAtDate(@Param("storeId") UUID storeId, @Param("date") LocalDate date);

    @Query("SELECT o FROM Order o WHERE o.store.id = :storeId AND o.createdAt >= :start AND o.createdAt < :end ORDER BY o.createdAt DESC")
    List<Order> findByStoreIdAndCreatedAtBetween(@Param("storeId") UUID storeId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}

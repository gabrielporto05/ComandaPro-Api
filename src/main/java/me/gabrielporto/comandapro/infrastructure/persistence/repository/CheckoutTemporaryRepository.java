package me.gabrielporto.comandapro.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import me.gabrielporto.comandapro.core.domain.checkout.CheckoutTemporary;

@Repository
public interface CheckoutTemporaryRepository extends JpaRepository<CheckoutTemporary, UUID> {

    Optional<CheckoutTemporary> findByIdAndProcessedFalse(UUID id);

    @Modifying
    @Query("DELETE FROM CheckoutTemporary c WHERE c.expiresAt < :now")
    void deleteExpired(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE CheckoutTemporary c SET c.processed = true WHERE c.id = :id")
    void marcarComoProcessado(@Param("id") UUID id);
}

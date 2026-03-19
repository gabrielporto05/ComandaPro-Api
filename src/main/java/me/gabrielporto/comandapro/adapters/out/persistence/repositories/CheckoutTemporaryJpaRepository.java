package me.gabrielporto.comandapro.adapters.out.persistence.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import me.gabrielporto.comandapro.adapters.out.persistence.entities.CheckoutTemporaryEntity;

public interface CheckoutTemporaryJpaRepository extends JpaRepository<CheckoutTemporaryEntity, UUID> {

    Optional<CheckoutTemporaryEntity> findByIdAndProcessedFalse(UUID id);
}

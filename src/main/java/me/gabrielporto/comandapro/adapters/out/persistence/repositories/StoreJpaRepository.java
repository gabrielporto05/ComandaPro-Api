package me.gabrielporto.comandapro.adapters.out.persistence.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import me.gabrielporto.comandapro.adapters.out.persistence.entities.StoreEntity;
import me.gabrielporto.comandapro.core.domain.store.StoreStatus;

public interface StoreJpaRepository extends JpaRepository<StoreEntity, UUID> {

    Optional<StoreEntity> findBySlug(String slug);

    Optional<StoreEntity> findByUserId(UUID userId);

    boolean existsBySlug(String slug);

    List<StoreEntity> findAllByStatus(StoreStatus status);

    List<StoreEntity> findAllByUserIdIn(List<UUID> userIds);
}

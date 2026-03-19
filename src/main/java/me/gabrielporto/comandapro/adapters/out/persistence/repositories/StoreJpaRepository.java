package me.gabrielporto.comandapro.adapters.out.persistence.repositories;

import java.util.Optional;
import java.util.UUID;
import me.gabrielporto.comandapro.adapters.out.persistence.entities.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreJpaRepository extends JpaRepository<StoreEntity, UUID> {
    Optional<StoreEntity> findBySlug(String slug);
}

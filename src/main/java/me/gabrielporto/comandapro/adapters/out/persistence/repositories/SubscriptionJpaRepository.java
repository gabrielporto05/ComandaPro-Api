package me.gabrielporto.comandapro.adapters.out.persistence.repositories;

import java.util.Optional;
import java.util.UUID;
import me.gabrielporto.comandapro.adapters.out.persistence.entities.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionJpaRepository extends JpaRepository<SubscriptionEntity, UUID> {
    Optional<SubscriptionEntity> findByStoreId(UUID storeId);
}

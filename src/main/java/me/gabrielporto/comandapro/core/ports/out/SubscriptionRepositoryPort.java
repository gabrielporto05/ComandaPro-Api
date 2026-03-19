package me.gabrielporto.comandapro.core.ports.out;

import java.util.Optional;
import java.util.UUID;
import me.gabrielporto.comandapro.core.domain.subscription.Subscription;

public interface SubscriptionRepositoryPort {
    Subscription save(Subscription subscription);
    Optional<Subscription> findByStoreId(UUID storeId);
}

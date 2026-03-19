package me.gabrielporto.comandapro.core.ports.out;

import java.util.Optional;
import java.util.UUID;

import me.gabrielporto.comandapro.core.domain.store.Store;
import me.gabrielporto.comandapro.core.domain.store.StoreStatus;

public interface StoreRepositoryPort {

    Store save(Store store);

    Optional<Store> findById(UUID id);

    Optional<Store> findBySlug(String slug);

    Optional<Store> findByUserId(UUID userId);

    boolean existsBySlug(String slug);

    java.util.List<Store> findAllByStatus(StoreStatus status);

    java.util.List<Store> findAllByUserIdIn(java.util.List<UUID> userIds);
}

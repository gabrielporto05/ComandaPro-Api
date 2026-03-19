package me.gabrielporto.comandapro.core.ports.out;

import java.util.Optional;
import java.util.UUID;

import me.gabrielporto.comandapro.core.domain.store.Store;

public interface StoreRepositoryPort {

    Store save(Store store);

    Optional<Store> findById(UUID id);

    Optional<Store> findBySlug(String slug);
}

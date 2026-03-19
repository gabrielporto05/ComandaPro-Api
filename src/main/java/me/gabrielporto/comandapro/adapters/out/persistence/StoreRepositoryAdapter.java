package me.gabrielporto.comandapro.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import me.gabrielporto.comandapro.adapters.out.persistence.mappers.StorePersistenceMapper;
import me.gabrielporto.comandapro.adapters.out.persistence.repositories.StoreJpaRepository;
import me.gabrielporto.comandapro.core.domain.store.Store;
import me.gabrielporto.comandapro.core.domain.store.StoreStatus;
import me.gabrielporto.comandapro.core.ports.out.StoreRepositoryPort;

@Component
public class StoreRepositoryAdapter implements StoreRepositoryPort {

    private final StoreJpaRepository repository;

    public StoreRepositoryAdapter(StoreJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Store save(Store store) {
        var entity = StorePersistenceMapper.toEntity(store);
        var saved = repository.save(entity);
        return StorePersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Store> findById(UUID id) {
        return repository.findById(id).map(StorePersistenceMapper::toDomain);
    }

    @Override
    public Optional<Store> findBySlug(String slug) {
        return repository.findBySlug(slug).map(StorePersistenceMapper::toDomain);
    }

    @Override
    public Optional<Store> findByUserId(UUID userId) {
        return repository.findByUserId(userId).map(StorePersistenceMapper::toDomain);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return repository.existsBySlug(slug);
    }

    @Override
    public List<Store> findAllByStatus(StoreStatus status) {
        return repository.findAllByStatus(status).stream().map(StorePersistenceMapper::toDomain).toList();
    }

    @Override
    public List<Store> findAllByUserIdIn(List<UUID> userIds) {
        return repository.findAllByUserIdIn(userIds).stream().map(StorePersistenceMapper::toDomain).toList();
    }
}

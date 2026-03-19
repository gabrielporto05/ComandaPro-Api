package me.gabrielporto.comandapro.core.services;

import java.util.UUID;

import me.gabrielporto.comandapro.core.domain.store.Store;
import me.gabrielporto.comandapro.core.ports.in.StoreManagementUseCase;
import me.gabrielporto.comandapro.core.ports.out.StoreRepositoryPort;

public class StoreService implements StoreManagementUseCase {

    private final StoreRepositoryPort storeRepository;

    public StoreService(StoreRepositoryPort storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    public Store create(Store store) {
        return storeRepository.save(store);
    }

    @Override
    public Store update(UUID id, Store store) {
        storeRepository.findById(id).orElseThrow();
        // por simplicidade, sobrescreve; regras adicionais podem ser adicionadas
        return storeRepository.save(store);
    }

    @Override
    public void activate(UUID id) {
        Store store = storeRepository.findById(id).orElseThrow();
        store.activate();
        storeRepository.save(store);
    }

    @Override
    public void suspend(UUID id) {
        Store store = storeRepository.findById(id).orElseThrow();
        store.suspend();
        storeRepository.save(store);
    }

    @Override
    public void cancel(UUID id) {
        Store store = storeRepository.findById(id).orElseThrow();
        store.cancel();
        storeRepository.save(store);
    }
}

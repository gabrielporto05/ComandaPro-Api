package me.gabrielporto.comandapro.core.ports.in;

import java.util.UUID;
import me.gabrielporto.comandapro.core.domain.store.Store;

public interface StoreManagementUseCase {
    Store create(Store store);
    Store update(UUID id, Store store);
    void activate(UUID id);
    void suspend(UUID id);
    void cancel(UUID id);
}

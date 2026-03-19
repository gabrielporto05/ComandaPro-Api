package me.gabrielporto.comandapro.adapters.out.persistence.mappers;

import me.gabrielporto.comandapro.adapters.out.persistence.entities.StoreEntity;
import me.gabrielporto.comandapro.core.domain.store.Store;

public class StorePersistenceMapper {

    public static StoreEntity toEntity(Store store) {
        StoreEntity entity = new StoreEntity();
        return entity;
    }

    public static Store toDomain(StoreEntity entity) {
        return new Store();
    }
}

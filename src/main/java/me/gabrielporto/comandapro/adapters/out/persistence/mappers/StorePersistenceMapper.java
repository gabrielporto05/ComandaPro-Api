package me.gabrielporto.comandapro.adapters.out.persistence.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.gabrielporto.comandapro.adapters.out.persistence.entities.StoreEntity;
import me.gabrielporto.comandapro.core.domain.order.PaymentMethod;
import me.gabrielporto.comandapro.core.domain.store.Store;
import me.gabrielporto.comandapro.core.domain.store.StoreHours;
import me.gabrielporto.comandapro.core.domain.store.StoreStatus;

public class StorePersistenceMapper {

    public static StoreEntity toEntity(Store store) {
        StoreEntity entity = new StoreEntity();
        entity.setId(store.getId());
        entity.setName(store.getName());
        entity.setSlug(store.getSlug());
        if (store.getOwner() != null) {
            entity.setOwner(UserPersistenceMapper.toEntity(store.getOwner()));
        }
        entity.setEmail(store.getEmail());
        entity.setTel(store.getTel());
        entity.setDescription(store.getDescription());
        entity.setAddress(store.getAddress());
        entity.setPhotos(new ArrayList<>(store.getPhotos()));
        entity.setFeeDelivery(store.getFeeDelivery());
        entity.setCategories(new ArrayList<>(store.getCategories()));
        entity.setPaymentMethods(store.getPaymentMethods());
        entity.setStatus(store.getStatus());
        entity.setCreatedAt(store.getCreatedAt());
        entity.setUpdatedAt(store.getUpdatedAt());
        if (store.getHours() != null) {
            entity.setHours(store.getHours().stream()
                    .map(h -> StoreHoursPersistenceMapper.toEntity(h, entity))
                    .collect(Collectors.toList()));
        }
        return entity;
    }

    public static Store toDomain(StoreEntity entity) {
        if (entity == null) return null;
        Store store = new Store();
        store.setId(entity.getId());
        store.setName(entity.getName());
        store.setSlug(entity.getSlug());
        store.setOwner(UserPersistenceMapper.toDomain(entity.getOwner()));
        store.setEmail(entity.getEmail());
        store.setTel(entity.getTel());
        store.setDescription(entity.getDescription());
        store.setAddress(entity.getAddress());
        store.setPhotos(entity.getPhotos() != null ? new ArrayList<>(entity.getPhotos()) : new ArrayList<>());
        store.setFeeDelivery(entity.getFeeDelivery());
        store.setCategories(entity.getCategories() != null ? new ArrayList<>(entity.getCategories()) : new ArrayList<>());
        store.setPaymentMethods(entity.getPaymentMethods() != null ? entity.getPaymentMethods() : new PaymentMethod[0]);
        store.setStatus(entity.getStatus() != null ? entity.getStatus() : StoreStatus.ACTIVE);
        List<StoreHours> hours = entity.getHours() != null
                ? entity.getHours().stream().map(StoreHoursPersistenceMapper::toDomain).toList()
                : new ArrayList<>();
        store.setHours(hours);
        store.setCreatedAt(entity.getCreatedAt());
        store.setUpdatedAt(entity.getUpdatedAt());
        return store;
    }
}

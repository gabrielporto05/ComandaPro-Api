package me.gabrielporto.comandapro.adapters.out.persistence.mappers;

import java.time.LocalTime;

import me.gabrielporto.comandapro.adapters.out.persistence.entities.StoreEntity;
import me.gabrielporto.comandapro.adapters.out.persistence.entities.StoreHoursEntity;
import me.gabrielporto.comandapro.core.domain.store.StoreHours;

public class StoreHoursPersistenceMapper {

    public static StoreHoursEntity toEntity(StoreHours hours, StoreEntity store) {
        StoreHoursEntity entity = new StoreHoursEntity();
        entity.setId(hours.getId());
        entity.setStore(store);
        entity.setDayOfWeek(hours.getDayOfWeek());
        entity.setOpenTime(hours.getOpenTime());
        entity.setCloseTime(hours.getCloseTime());
        entity.setClosed(Boolean.TRUE.equals(hours.getIsClosed()));
        entity.setCreatedAt(hours.getCreatedAt());
        entity.setUpdatedAt(hours.getUpdatedAt());
        return entity;
    }

    public static StoreHours toDomain(StoreHoursEntity entity) {
        StoreHours hours = new StoreHours();
        hours.setDayOfWeek(entity.getDayOfWeek());
        hours.setOpenTime(entity.getOpenTime());
        hours.setCloseTime(entity.getCloseTime());
        hours.setIsClosed(entity.isClosed());
        hours.setCreatedAt(entity.getCreatedAt());
        hours.setUpdatedAt(entity.getUpdatedAt());
        return hours;
    }
}

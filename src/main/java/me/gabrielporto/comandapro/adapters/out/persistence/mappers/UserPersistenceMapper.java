package me.gabrielporto.comandapro.adapters.out.persistence.mappers;

import me.gabrielporto.comandapro.adapters.out.persistence.entities.UserEntity;
import me.gabrielporto.comandapro.core.domain.user.User;

public class UserPersistenceMapper {
    public static UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        // mapear campos conforme necessidade
        return entity;
    }

    public static User toDomain(UserEntity entity) {
        return new User();
    }
}

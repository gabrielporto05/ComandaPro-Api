package me.gabrielporto.comandapro.adapters.out.persistence.mappers;

import me.gabrielporto.comandapro.adapters.out.persistence.entities.UserEntity;
import me.gabrielporto.comandapro.core.domain.user.User;

public class UserPersistenceMapper {

    public static UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setTel(user.getTel());
        entity.setPassword(user.getPassword());
        entity.setRole(user.getRole());
        entity.setStatus(user.getStatus());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        return entity;
    }

    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        User user = new User();
        user.setId(entity.getId());
        user.setName(entity.getName());
        user.setEmail(entity.getEmail());
        user.setTel(entity.getTel());
        user.setPassword(entity.getPassword());
        user.setRole(entity.getRole());
        user.setStatus(entity.getStatus());
        user.setCreatedAt(entity.getCreatedAt());
        user.setUpdatedAt(entity.getUpdatedAt());
        return user;
    }
}

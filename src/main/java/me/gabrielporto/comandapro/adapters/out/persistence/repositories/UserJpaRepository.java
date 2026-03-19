package me.gabrielporto.comandapro.adapters.out.persistence.repositories;

import java.util.Optional;
import java.util.UUID;
import me.gabrielporto.comandapro.adapters.out.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
}

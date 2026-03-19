package me.gabrielporto.comandapro.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import me.gabrielporto.comandapro.adapters.out.persistence.entities.UserEntity;
import me.gabrielporto.comandapro.adapters.out.persistence.mappers.UserPersistenceMapper;
import me.gabrielporto.comandapro.adapters.out.persistence.repositories.UserJpaRepository;
import me.gabrielporto.comandapro.core.domain.user.User;
import me.gabrielporto.comandapro.core.ports.out.UserRepositoryPort;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository repository;

    public UserRepositoryAdapter(UserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserPersistenceMapper.toEntity(user);
        repository.save(entity);
        return UserPersistenceMapper.toDomain(entity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, UUID id) {
        return repository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public java.util.List<User> findAllUsers() {
        return repository.findAll().stream().map(UserPersistenceMapper::toDomain).toList();
    }
}

package me.gabrielporto.comandapro.core.ports.out;

import java.util.Optional;
import java.util.UUID;
import me.gabrielporto.comandapro.core.domain.user.User;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
}

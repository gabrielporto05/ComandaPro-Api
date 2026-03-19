package me.gabrielporto.comandapro.core.services;

import me.gabrielporto.comandapro.core.domain.user.User;
import me.gabrielporto.comandapro.core.ports.in.CreateOwnerUseCase;
import me.gabrielporto.comandapro.core.ports.out.UserRepositoryPort;

public class OwnerService implements CreateOwnerUseCase {

    private final UserRepositoryPort userRepository;

    public OwnerService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }
}

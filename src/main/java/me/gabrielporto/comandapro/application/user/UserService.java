package me.gabrielporto.comandapro.application.user;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.gabrielporto.comandapro.core.domain.store.Store;
import me.gabrielporto.comandapro.core.domain.user.User;
import me.gabrielporto.comandapro.infrastructure.persistence.repository.StoreJpaRepository;
import me.gabrielporto.comandapro.infrastructure.persistence.repository.UserJpaRepository;
import me.gabrielporto.comandapro.shared.exception.BusinessException;

@Service
public class UserService {

    private final UserJpaRepository userRepository;
    private final StoreJpaRepository storeRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserJpaRepository userRepository,
            StoreJpaRepository storeRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User updateProfile(UUID userId, String name, String email, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (email != null && !email.equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(email, userId)) {
                throw new BusinessException("Email já está em uso");
            }
            user.setEmail(email);
        }

        if (name != null) {
            user.setName(name);
        }

        if (newPassword != null && !newPassword.isBlank()) {
            if (currentPassword == null || currentPassword.isBlank()) {
                throw new BusinessException("Senha atual é obrigatória para alterar a senha");
            }
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                throw new BusinessException("Senha atual incorreta");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> listAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Map<UUID, Store> findStoresByUserIds(List<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return storeRepository.findAllByUserIdIn(userIds).stream()
                .collect(Collectors.toMap(s -> s.getUser().getId(), s -> s));
    }
}

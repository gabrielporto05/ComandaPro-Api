package me.gabrielporto.comandapro.application.store;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.gabrielporto.comandapro.core.domain.store.Store;
import me.gabrielporto.comandapro.core.domain.store.StoreStatus;
import me.gabrielporto.comandapro.core.domain.user.User;
import me.gabrielporto.comandapro.infrastructure.persistence.repository.StoreJpaRepository;
import me.gabrielporto.comandapro.infrastructure.persistence.repository.UserJpaRepository;
import me.gabrielporto.comandapro.shared.exception.BusinessException;

@Service
public class CreateStoreUseCase {

    private final StoreJpaRepository storeRepository;
    private final UserJpaRepository userRepository;
    private final StoreService storeService;

    public CreateStoreUseCase(
            StoreJpaRepository storeRepository,
            UserJpaRepository userRepository,
            StoreService storeService) {
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
        this.storeService = storeService;
    }

    @Transactional
    public Store execute(UUID userId, String name, String email, String tel,
            String description, String address, String[] photos, Double feeDelivery) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (storeRepository.findByUserId(userId).isPresent()) {
            throw new BusinessException("Usuário já possui uma loja cadastrada");
        }

        String slug = storeService.generateSlug(name);

        Store store = Store.builder()
                .name(name)
                .slug(slug)
                .user(user)
                .email(email != null ? email : user.getEmail())
                .tel(tel != null ? tel : user.getTel())
                .status(StoreStatus.ACTIVE)
                .description(description)
                .address(address)
                .feeDelivery(feeDelivery != null ? feeDelivery : 0.0)
                .createdAt(LocalDateTime.now())
                .build();

        return storeRepository.save(store);
    }
}

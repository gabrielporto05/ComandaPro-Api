package me.gabrielporto.comandapro.application.store;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.gabrielporto.comandapro.core.domain.store.Store;
import me.gabrielporto.comandapro.infrastructure.persistence.repository.StoreJpaRepository;
import me.gabrielporto.comandapro.shared.exception.BusinessException;

@Service
public class UpdateStoreUseCase {

    private final StoreJpaRepository storeRepository;

    public UpdateStoreUseCase(StoreJpaRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Transactional
    public Store execute(UUID storeId, UUID userId, String name, String slug, String email,
            String tel, String description, String address,
            String[] photos, Double feeDelivery) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException("Loja não encontrada"));

        if (!store.getUser().getId().equals(userId)) {
            throw new BusinessException("Você não tem permissão para editar esta loja");
        }

        if (name != null && !name.trim().isEmpty()) {
            store.setName(name);
        }

        if (slug != null && !slug.trim().isEmpty()) {

            store.setSlug(slug);
        }

        if (email != null && !email.trim().isEmpty()) {
            store.setEmail(email);
        }

        if (tel != null && !tel.trim().isEmpty()) {
            store.setTel(tel);
        }

        if (description != null) {
            store.setDescription(description);
        }

        if (address != null) {
            store.setAddress(address);
        }

        if (feeDelivery != null) {
            store.setFeeDelivery(feeDelivery);
        }

        return storeRepository.save(store);
    }
}

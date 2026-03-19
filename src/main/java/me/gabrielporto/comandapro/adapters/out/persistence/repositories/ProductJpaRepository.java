package me.gabrielporto.comandapro.adapters.out.persistence.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import me.gabrielporto.comandapro.adapters.out.persistence.entities.ProductEntity;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {

    List<ProductEntity> findByStoreIdAndIsAvailableTrue(UUID storeId);

    List<ProductEntity> findByStoreIdAndCategoryAndIsAvailable(UUID storeId, String category, Boolean available);

    List<ProductEntity> findByStoreIdAndCategory(UUID storeId, String category);

    List<ProductEntity> findByStoreIdAndIsAvailable(UUID storeId, Boolean available);

    List<ProductEntity> findByStoreId(UUID storeId);
}

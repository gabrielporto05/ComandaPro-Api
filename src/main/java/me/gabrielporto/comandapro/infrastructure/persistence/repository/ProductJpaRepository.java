package me.gabrielporto.comandapro.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.gabrielporto.comandapro.core.domain.store.Product;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, UUID> {

    List<Product> findByStoreId(UUID storeId);

    List<Product> findByStoreIdAndIsAvailable(UUID storeId, Boolean isAvailable);

    List<Product> findByStoreIdAndCategory(UUID storeId, String category);

    List<Product> findByStoreIdAndCategoryAndIsAvailable(UUID storeId, String category, Boolean isAvailable);

    List<Product> findByStoreIdAndIsAvailableTrue(UUID storeId);

    List<Product> findByStoreIdAndIsHighlightTrue(UUID storeId);

    boolean existsByStoreIdAndName(UUID storeId, String name);
}

package me.gabrielporto.comandapro.application.store;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.gabrielporto.comandapro.core.domain.store.Product;
import me.gabrielporto.comandapro.core.domain.store.Store;
import me.gabrielporto.comandapro.infrastructure.persistence.repository.ProductJpaRepository;
import me.gabrielporto.comandapro.infrastructure.persistence.repository.StoreJpaRepository;
import me.gabrielporto.comandapro.shared.exception.BusinessException;

@Service
public class ManageProductsUseCase {

    private final ProductJpaRepository productRepository;
    private final StoreJpaRepository storeRepository;

    public ManageProductsUseCase(
            ProductJpaRepository productRepository,
            StoreJpaRepository storeRepository) {
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> listProductsByStoreSlug(String storeSlug) {
        Store store = storeRepository.findBySlug(storeSlug)
                .orElseThrow(() -> new BusinessException("Loja não encontrada"));

        return productRepository.findByStoreIdAndIsAvailableTrue(store.getId());
    }

    @Transactional(readOnly = true)
    public Product getProduct(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("Produto não encontrado"));
    }

    @Transactional
    public Product createProduct(UUID storeId, UUID userId, String name, String description,
            Double price, String photoUrl, String category, Boolean isHighlight) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException("Loja não encontrada"));

        if (!store.getUser().getId().equals(userId)) {
            throw new BusinessException("Você não tem permissão para adicionar produtos nesta loja");
        }

        Product product = Product.builder()
                .store(store)
                .name(name)
                .description(description)
                .price(price)
                .photoUrl(photoUrl)
                .category(category)
                .isAvailable(true)
                .isHighlight(isHighlight != null ? isHighlight : false)
                .createdAt(LocalDateTime.now())
                .build();

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(UUID productId, UUID userId, String name, String description,
            Double price, String photoUrl, String category,
            Boolean isAvailable, Boolean isHighlight) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("Produto não encontrado"));

        if (!product.getStore().getUser().getId().equals(userId)) {
            throw new BusinessException("Você não tem permissão para editar este produto");
        }

        if (name != null && !name.trim().isEmpty()) {
            product.setName(name);
        }

        if (description != null) {
            product.setDescription(description);
        }

        if (price != null) {
            product.setPrice(price);
        }

        if (photoUrl != null) {
            product.setPhotoUrl(photoUrl);
        }

        if (category != null) {
            product.setCategory(category);
        }

        if (isAvailable != null) {
            product.setIsAvailable(isAvailable);
        }

        if (isHighlight != null) {
            product.setIsHighlight(isHighlight);
        }

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(UUID productId, UUID userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("Produto não encontrado"));

        if (!product.getStore().getUser().getId().equals(userId)) {
            throw new BusinessException("Você não tem permissão para deletar este produto");
        }

        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public List<Product> listProductsByStore(UUID storeId, UUID userId, String category, Boolean available) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException("Loja não encontrada"));

        if (!store.getUser().getId().equals(userId)) {
            throw new BusinessException("Você não tem permissão para ver produtos desta loja");
        }

        if (category != null && available != null) {
            return productRepository.findByStoreIdAndCategoryAndIsAvailable(storeId, category, available);
        } else if (category != null) {
            return productRepository.findByStoreIdAndCategory(storeId, category);
        } else if (available != null) {
            return productRepository.findByStoreIdAndIsAvailable(storeId, available);
        } else {
            return productRepository.findByStoreId(storeId);
        }
    }

    @Transactional
    public Product toggleAvailability(UUID productId, UUID userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("Produto não encontrado"));

        if (!product.getStore().getUser().getId().equals(userId)) {
            throw new BusinessException("Você não tem permissão para alterar este produto");
        }

        product.setIsAvailable(!product.getIsAvailable());
        return productRepository.save(product);
    }

    @Transactional
    public Product updatePhotoUrl(UUID productId, UUID userId, String photoUrl) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("Produto não encontrado"));

        if (!product.getStore().getUser().getId().equals(userId)) {
            throw new BusinessException("Você não tem permissão para alterar a foto deste produto");
        }

        product.setPhotoUrl(photoUrl);

        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }
}

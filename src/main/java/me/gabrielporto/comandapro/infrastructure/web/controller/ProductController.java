package me.gabrielporto.comandapro.infrastructure.web.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import me.gabrielporto.comandapro.application.store.ManageProductsUseCase;
import me.gabrielporto.comandapro.core.domain.store.Product;
import me.gabrielporto.comandapro.core.domain.user.User;
import me.gabrielporto.comandapro.infrastructure.web.dto.request.CreateProductRequest;
import me.gabrielporto.comandapro.infrastructure.web.dto.request.UpdateProductRequest;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.ApiResponse;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.ProductResponse;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    private final ManageProductsUseCase manageProductsUseCase;

    public ProductController(ManageProductsUseCase manageProductsUseCase) {
        this.manageProductsUseCase = manageProductsUseCase;
    }

    @GetMapping("/public/stores/{storeSlug}/products")
    public ResponseEntity<ApiResponse> listPublicProducts(@PathVariable String storeSlug) {
        List<Product> products = manageProductsUseCase.listProductsByStoreSlug(storeSlug);

        List<ProductResponse> response = products.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(new ApiResponse(
                "Produtos listados com sucesso",
                response
        ));
    }

    @GetMapping("/stores/{storeSlug}/all-products")
    public ResponseEntity<ApiResponse> listAllProductsForOwner(
            @PathVariable String storeSlug,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        List<Product> products = manageProductsUseCase.listAllProductsByStoreSlug(storeSlug, user.getId());

        List<ProductResponse> response = products.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(new ApiResponse(
                "Todos os produtos listados com sucesso",
                response
        ));
    }

    @GetMapping("/public/products/{productId}")
    public ResponseEntity<ApiResponse> getPublicProduct(@PathVariable UUID productId) {
        Product product = manageProductsUseCase.getProduct(productId);

        return ResponseEntity.ok(new ApiResponse(
                "Produto encontrado",
                toResponse(product)
        ));
    }

    @PostMapping("/stores/{storeId}/products")
    public ResponseEntity<ApiResponse> createProduct(
            @PathVariable UUID storeId,
            @Valid @RequestBody CreateProductRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Product product = manageProductsUseCase.createProduct(
                storeId,
                user.getId(),
                request.name(),
                request.description(),
                request.price(),
                request.photoUrl(),
                request.category(),
                request.isHighlight()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(
                        "Produto criado com sucesso",
                        toResponse(product)
                ));
    }

    @PutMapping("/stores/{storeId}/products/{productId}")
    public ResponseEntity<ApiResponse> updateProduct(
            @PathVariable UUID storeId,
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Product product = manageProductsUseCase.updateProduct(
                productId,
                user.getId(),
                request.name(),
                request.description(),
                request.price(),
                request.photoUrl(),
                request.category(),
                request.isAvailable(),
                request.isHighlight()
        );

        return ResponseEntity.ok(new ApiResponse(
                "Produto atualizado com sucesso",
                toResponse(product)
        ));
    }

    @DeleteMapping("/stores/{storeId}/products/{productId}")
    public ResponseEntity<ApiResponse> deleteProduct(
            @PathVariable UUID storeId,
            @PathVariable UUID productId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        manageProductsUseCase.deleteProduct(productId, user.getId());

        return ResponseEntity.ok(new ApiResponse(
                "Produto deletado com sucesso",
                null
        ));
    }

    @PatchMapping("/stores/{storeId}/products/{productId}/toggle-availability")
    public ResponseEntity<ApiResponse> toggleAvailability(
            @PathVariable UUID storeId,
            @PathVariable UUID productId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Product product = manageProductsUseCase.toggleAvailability(productId, user.getId());

        return ResponseEntity.ok(new ApiResponse(
                "Disponibilidade alterada com sucesso",
                toResponse(product)
        ));
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getStore().getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getPhotoUrl(),
                product.getCategory(),
                product.getIsAvailable(),
                product.getIsHighlight(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}

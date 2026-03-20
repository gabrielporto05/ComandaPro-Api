package me.gabrielporto.comandapro.infrastructure.web.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import me.gabrielporto.comandapro.application.store.ManageProductsUseCase;
import me.gabrielporto.comandapro.core.domain.user.User;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.ApiResponse;
import me.gabrielporto.comandapro.shared.exception.BusinessException;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/products/{productId}/photos")
public class ProductPhotoController {

    private final ManageProductsUseCase manageProductsUseCase;

    @Value("${app.upload.path:}")
    private String uploadPath;

    @Value("${app.base-url:}")
    private String baseUrl;

    public ProductPhotoController(ManageProductsUseCase manageProductsUseCase) {
        this.manageProductsUseCase = manageProductsUseCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> uploadPhoto(
            @PathVariable UUID storeId,
            @PathVariable UUID productId,
            @RequestParam("photo") MultipartFile file,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        validateFile(file);

        try {
            Path uploadDir = Paths.get(uploadPath, storeId.toString(), "products");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String fileName = generateFileName(file);
            Path filePath = uploadDir.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String photoUrl = String.format("%s/uploads/%s/products/%s", baseUrl, storeId, fileName);

            manageProductsUseCase.updatePhotoUrl(productId, user.getId(), photoUrl);

            return ResponseEntity.ok(new ApiResponse(
                    "Foto do produto enviada com sucesso",
                    photoUrl
            ));

        } catch (IOException e) {
            throw new BusinessException("Erro ao salvar foto do produto: " + e.getMessage());
        }
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<ApiResponse> deletePhoto(
            @PathVariable UUID storeId,
            @PathVariable UUID productId,
            @PathVariable String fileName,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        try {
            manageProductsUseCase.updatePhotoUrl(productId, user.getId(), null);

            Path filePath = Paths.get(uploadPath, storeId.toString(), "products", fileName);
            Files.deleteIfExists(filePath);

            return ResponseEntity.ok(new ApiResponse(
                    "Foto do produto removida com sucesso",
                    null
            ));

        } catch (IOException e) {
            throw new BusinessException("Erro ao remover arquivo: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("Arquivo vazio");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("Apenas imagens são permitidas");
        }

        if (file.getSize() > 2 * 1024 * 1024) {
            throw new BusinessException("Tamanho máximo: 2MB");
        }
    }

    private String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        return UUID.randomUUID().toString() + extension;
    }
}

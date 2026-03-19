package me.gabrielporto.comandapro.infrastructure.web.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import me.gabrielporto.comandapro.application.store.StoreService;
import me.gabrielporto.comandapro.core.domain.user.User;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.ApiResponse;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.UploadPhotoResponse;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.UploadResult;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/photos")
public class StorePhotoController {

    private final StoreService storeService;

    @Value("${app.upload.path:}")
    private String uploadPath;

    @Value("${app.base-url:}")
    private String baseUrl;

    public StorePhotoController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> uploadPhotos(
            @PathVariable UUID storeId,
            @RequestParam("photos") List<MultipartFile> files,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        UploadResult result = storeService.uploadPhotos(
                storeId,
                user.getId(),
                files,
                baseUrl,
                uploadPath
        );

        return ResponseEntity.ok(new ApiResponse(
                "Fotos enviadas com sucesso",
                new UploadPhotoResponse(
                        result.photoUrls().isEmpty() ? null : result.photoUrls().get(0),
                        result.allPhotos()
                )
        ));
    }
}

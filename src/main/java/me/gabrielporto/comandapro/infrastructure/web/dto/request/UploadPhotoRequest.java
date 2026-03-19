package me.gabrielporto.comandapro.infrastructure.web.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

public record UploadPhotoRequest(
        @NotNull(message = "Arquivo é obrigatório")
        MultipartFile photo
        ) {

}

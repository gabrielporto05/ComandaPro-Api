package me.gabrielporto.comandapro.infrastructure.web.dto.response;

import java.util.List;

public record UploadPhotoResponse(
        String photoUrl,
        List<String> allPhotos
        ) {

}

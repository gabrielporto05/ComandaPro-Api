package me.gabrielporto.comandapro.infrastructure.web.dto.response;

import java.util.List;

public record UploadResult(
        List<String> photoUrls,
        List<String> allPhotos
        ) {

}

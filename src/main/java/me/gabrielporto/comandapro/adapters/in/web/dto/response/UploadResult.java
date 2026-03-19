package me.gabrielporto.comandapro.adapters.in.web.dto.response;

import java.util.List;

public record UploadResult(List<String> photoUrls, List<String> allPhotos) {
}

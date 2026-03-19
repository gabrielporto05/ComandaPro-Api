package me.gabrielporto.comandapro.adapters.in.web.dto.response;

import java.util.List;

public record UploadPhotoResponse(String coverPhoto, List<String> photos) {
}

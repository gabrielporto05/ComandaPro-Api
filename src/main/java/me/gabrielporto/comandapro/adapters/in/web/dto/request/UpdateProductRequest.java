package me.gabrielporto.comandapro.adapters.in.web.dto.request;

public record UpdateProductRequest(
        String name,
        String description,
        Double price,
        String photoUrl,
        String category,
        Boolean isAvailable,
        Boolean isHighlight
) {
}

package me.gabrielporto.comandapro.adapters.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductRequest(
        @NotBlank String name,
        String description,
        @NotNull Double price,
        String photoUrl,
        String category,
        Boolean isHighlight
) {
}

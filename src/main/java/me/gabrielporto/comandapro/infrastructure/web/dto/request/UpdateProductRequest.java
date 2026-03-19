package me.gabrielporto.comandapro.infrastructure.web.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateProductRequest(
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        String name,
        @Size(max = 500, message = "Descrição muito longa")
        String description,
        @Positive(message = "Preço deve ser maior que zero")
        Double price,
        String photoUrl,
        String category,
        Boolean isAvailable,
        Boolean isHighlight
        ) {

}

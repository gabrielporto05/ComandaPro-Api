package me.gabrielporto.comandapro.adapters.in.web.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateStoreHoursRequest(
        @NotNull @Min(0) @Max(6) Integer dayOfWeek,
        @NotBlank String openTime,
        @NotBlank String closeTime,
        Boolean isClosed
) {
}

package me.gabrielporto.comandapro.infrastructure.web.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateStoreHoursRequest(
        @NotNull(message = "Dia da semana é obrigatório")
        @Min(value = 0, message = "Dia deve ser entre 0 (domingo) e 6 (sábado)")
        @Max(value = 6, message = "Dia deve ser entre 0 (domingo) e 6 (sábado)")
        Integer dayOfWeek,
        @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Formato de hora inválido (use HH:mm)")
        String openTime,
        @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Formato de hora inválido (use HH:mm)")
        String closeTime,
        Boolean isClosed
        ) {

}

package me.gabrielporto.comandapro.adapters.in.web.dto.response;

import java.util.UUID;

public record HoursResponse(
        UUID id,
        Integer dayOfWeek,
        String dayName,
        String openTime,
        String closeTime,
        Boolean isClosed,
        String formattedTime
) {
}

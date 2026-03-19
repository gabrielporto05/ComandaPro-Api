package me.gabrielporto.comandapro.adapters.in.web.dto.response;

import java.util.UUID;

public record AdminStoreSummaryResponse(
        UUID id,
        String name,
        String slug,
        String email,
        String tel,
        String status
) {
}

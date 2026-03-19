package me.gabrielporto.comandapro.adapters.in.web.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AdminUserResponse(
        UUID id,
        String name,
        String email,
        String tel,
        String role,
        String status,
        OffsetDateTime createdAt,
        AdminStoreSummaryResponse store
) {
}

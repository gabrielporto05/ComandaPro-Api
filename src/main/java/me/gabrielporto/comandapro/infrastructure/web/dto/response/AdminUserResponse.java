package me.gabrielporto.comandapro.infrastructure.web.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminUserResponse(
        UUID id,
        String name,
        String email,
        String tel,
        String role,
        String status,
        LocalDateTime createdAt,
        AdminStoreSummaryResponse store
        ) {

}

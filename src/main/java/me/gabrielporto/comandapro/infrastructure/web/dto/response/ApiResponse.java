package me.gabrielporto.comandapro.infrastructure.web.dto.response;

public record ApiResponse(
        String message,
        Object data
        ) {

}

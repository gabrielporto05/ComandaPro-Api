package me.gabrielporto.comandapro.infrastructure.web.dto.response;

import java.util.UUID;

public record CheckoutResponse(
        UUID checkoutId,
        String checkoutUrl,
        int expiresIn
        ) {

}

package me.gabrielporto.comandapro.adapters.in.web.dto.response;

import java.util.UUID;

public record CheckoutResponse(UUID checkoutId, String checkoutUrl, int expiresIn) {
}

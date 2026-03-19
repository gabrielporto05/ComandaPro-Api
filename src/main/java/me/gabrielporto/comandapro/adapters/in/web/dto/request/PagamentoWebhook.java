package me.gabrielporto.comandapro.adapters.in.web.dto.request;

import java.util.UUID;

public record PagamentoWebhook(
        UUID checkoutId,
        String status,
        String paymentId,
        Double transactionAmount
) {
}

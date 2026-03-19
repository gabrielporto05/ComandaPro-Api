package me.gabrielporto.comandapro.infrastructure.web.dto.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PagamentoWebhook(
        @JsonProperty("checkout_id")
        UUID checkoutId,
        String status,
        @JsonProperty("payment_id")
        String paymentId,
        @JsonProperty("transaction_amount")
        Double transactionAmount
        ) {

}

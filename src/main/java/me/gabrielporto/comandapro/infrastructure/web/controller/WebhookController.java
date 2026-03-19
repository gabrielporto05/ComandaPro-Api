package me.gabrielporto.comandapro.infrastructure.web.controller;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import me.gabrielporto.comandapro.application.checkout.CheckoutService;
import me.gabrielporto.comandapro.infrastructure.payment.MercadoPagoPaymentService;
import me.gabrielporto.comandapro.infrastructure.web.dto.request.PagamentoWebhook;
import tools.jackson.databind.JsonNode;

@Slf4j
@RestController
@RequestMapping("/api/v1/public/webhooks")
public class WebhookController {

    private final CheckoutService checkoutService;
    private final MercadoPagoPaymentService mercadoPagoPaymentService;

    @Value("${mercadopago.webhook-secret:}")
    private String webhookSecret;

    public WebhookController(
            CheckoutService checkoutService,
            MercadoPagoPaymentService mercadoPagoPaymentService) {
        this.checkoutService = checkoutService;
        this.mercadoPagoPaymentService = mercadoPagoPaymentService;
    }

    @PostMapping("/pagamento")
    public ResponseEntity<Void> processarPagamento(
            @RequestBody JsonNode payload,
            HttpServletRequest request) {
        log.info("Webhook recebido: {}", payload);

        /*  if (!validateWebhookSignature(payload, request)) {
            log.warn("Webhook Mercado Pago com assinatura inválida.");
            return ResponseEntity.status(401).build();
        } */
        if (payload.has("checkout_id")) {
            return processarWebhookLegado(payload);
        }

        if (payload.has("type") && payload.has("data") && payload.get("data").has("id")) {
            return processarWebhookMercadoPago(payload);
        }

        return ResponseEntity.ok().build();
    }

    private ResponseEntity<Void> processarWebhookLegado(JsonNode payload) {
        PagamentoWebhook webhook = new PagamentoWebhook(
                UUID.fromString(payload.get("checkout_id").asText()),
                payload.path("status").asText(null),
                payload.path("payment_id").asText(null),
                payload.hasNonNull("transaction_amount") ? payload.get("transaction_amount").asDouble() : null
        );

        log.info("Webhook legado recebido: checkoutId={}, status={}", webhook.checkoutId(), webhook.status());

        if ("APROVADO".equalsIgnoreCase(webhook.status())) {
            try {
                checkoutService.processarPagamentoAprovado(webhook.checkoutId());
                log.info("Pagamento processado com sucesso para checkout: {}", webhook.checkoutId());
            } catch (Exception e) {
                log.error("Erro ao processar pagamento: {}", e.getMessage(), e);
            }
        }

        return ResponseEntity.ok().build();
    }

    private ResponseEntity<Void> processarWebhookMercadoPago(JsonNode payload) {
        String type = payload.path("type").asText("");
        String dataId = payload.path("data").path("id").asText("");

        if (!"payment".equalsIgnoreCase(type) || !StringUtils.hasText(dataId)) {
            return ResponseEntity.ok().build();
        }

        try {
            MercadoPagoPaymentService.MpPaymentInfo payment
                    = mercadoPagoPaymentService.fetchPayment(Long.parseLong(dataId));

            if (!"approved".equalsIgnoreCase(payment.status())) {
                return ResponseEntity.ok().build();
            }

            String checkoutId = extractCheckoutId(payment.externalReference(), payment.metadata());
            if (!StringUtils.hasText(checkoutId)) {
                log.warn("Pagamento aprovado sem checkout_id: paymentId={}", dataId);
                return ResponseEntity.ok().build();
            }

            checkoutService.processarPagamentoAprovado(UUID.fromString(checkoutId));
            log.info("Pagamento processado com sucesso via MP: checkoutId={}, paymentId={}", checkoutId, dataId);
        } catch (Exception e) {
            log.error("Erro ao processar webhook Mercado Pago: {}", e.getMessage(), e);
        }

        return ResponseEntity.ok().build();
    }

    private String extractCheckoutId(String externalReference, Map<String, Object> metadata) {
        if (StringUtils.hasText(externalReference)) {
            return externalReference;
        }
        if (metadata == null) {
            return null;
        }
        Object value = metadata.get("checkout_id");
        return value == null ? null : value.toString();
    }

    private boolean validateWebhookSignature(JsonNode payload, HttpServletRequest request) {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            log.warn("mercadopago.webhook-secret não configurado. Pulando validação do webhook.");
            return true;
        }

        String xSignature = request.getHeader("x-signature");
        String xRequestId = request.getHeader("x-request-id");
        if (!StringUtils.hasText(xSignature) || !StringUtils.hasText(xRequestId)) {
            return false;
        }

        String ts = null;
        String v1 = null;
        for (String part : xSignature.split(",")) {
            String[] keyValue = part.split("=", 2);
            if (keyValue.length != 2) {
                continue;
            }
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();
            if ("ts".equals(key)) {
                ts = value;
            } else if ("v1".equals(key)) {
                v1 = value;
            }
        }

        if (!StringUtils.hasText(ts) || !StringUtils.hasText(v1)) {
            return false;
        }

        String dataId = request.getParameter("data.id");
        if (!StringUtils.hasText(dataId)) {
            dataId = payload.path("data").path("id").asText("");
        }
        dataId = dataId == null ? "" : dataId.toLowerCase();

        String template = "id:" + dataId + ";request-id:" + xRequestId + ";ts:" + ts + ";";
        String expected = hmacSha256(template, webhookSecret);

        return v1.equalsIgnoreCase(expected);
    }

    private String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) {
                    hex.append('0');
                }
                hex.append(h);
            }
            return hex.toString();
        } catch (Exception e) {
            log.error("Falha ao validar assinatura do webhook: {}", e.getMessage(), e);
            return "";
        }
    }
}

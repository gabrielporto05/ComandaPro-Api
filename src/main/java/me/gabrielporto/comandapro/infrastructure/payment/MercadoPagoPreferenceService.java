package me.gabrielporto.comandapro.infrastructure.payment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;

import me.gabrielporto.comandapro.infrastructure.web.dto.request.CheckoutRequest;
import me.gabrielporto.comandapro.shared.exception.BusinessException;

@Service
public class MercadoPagoPreferenceService {

    @Value("${mercadopago.notification-url:}")
    private String notificationUrl;

    @Value("${mercadopago.back-url.success:http://localhost:3000/checkout/success}")
    private String backUrlSuccess;

    @Value("${mercadopago.back-url.failure:http://localhost:3000/checkout/failure}")
    private String backUrlFailure;

    @Value("${mercadopago.back-url.pending:http://localhost:3000/checkout/pending}")
    private String backUrlPending;

    @Value("${mercadopago.use-sandbox:true}")
    private boolean useSandbox;

    public String createCheckoutUrl(CheckoutRequest request, UUID checkoutId) {
        try {

            PreferenceClient client = new PreferenceClient();

            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .title("Assinatura " + request.plan().getPeriodo() + " - comandapro")
                    .description("Plano " + request.plan().getPeriodo() + " - Acesso ao sistema de cardápio digital")
                    .quantity(1)
                    .currencyId("BRL")
                    .unitPrice(BigDecimal.valueOf(request.plan().getPreco()))
                    .build();

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(backUrlSuccess)
                    .failure(backUrlFailure)
                    .pending(backUrlPending)
                    .build();

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("checkout_id", checkoutId.toString());
            metadata.put("plan", request.plan().name());

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(List.of(item))
                    .backUrls(backUrls)
                    .notificationUrl(blankToNull(withWebhookSource(notificationUrl)))
                    .autoReturn("approved")
                    .externalReference(checkoutId.toString())
                    .metadata(metadata)
                    .build();

            Preference preference = client.create(preferenceRequest);
            String url = useSandbox ? preference.getSandboxInitPoint() : preference.getInitPoint();

            if (url == null || url.isBlank()) {
                url = preference.getInitPoint();
            }

            return url;

        } catch (MPApiException e) {
            throw new BusinessException("Erro ao criar preferência: " + e.getApiResponse().getContent());
        } catch (MPException e) {
            throw new BusinessException("Erro ao criar preferência: " + e.getMessage());
        }
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    private String withWebhookSource(String url) {
        if (url == null || url.isBlank()) {
            return url;
        }
        if (url.contains("source_news=")) {
            return url;
        }
        return url.contains("?") ? url + "&source_news=webhooks" : url + "?source_news=webhooks";
    }
}

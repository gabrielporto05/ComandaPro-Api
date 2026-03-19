package me.gabrielporto.comandapro.adapters.out.external.mercadopago;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;

import me.gabrielporto.comandapro.core.domain.order.Order;
import me.gabrielporto.comandapro.core.ports.out.PaymentGatewayPort;
import me.gabrielporto.comandapro.shared.BusinessException;

@Component
public class MercadoPagoAdapter implements PaymentGatewayPort {

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

    @Override
    public String createPreference(Order order) {
        try {
            PreferenceClient client = new PreferenceClient();

            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .title("Pedido " + order.getOrderCode())
                    .quantity(1)
                    .currencyId("BRL")
                    .unitPrice(order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO)
                    .build();

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(backUrlSuccess)
                    .failure(backUrlFailure)
                    .pending(backUrlPending)
                    .build();

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("order_id", order.getId().toString());

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(java.util.List.of(item))
                    .backUrls(backUrls)
                    .notificationUrl(notificationUrl)
                    .autoReturn("approved")
                    .externalReference(order.getId().toString())
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
}

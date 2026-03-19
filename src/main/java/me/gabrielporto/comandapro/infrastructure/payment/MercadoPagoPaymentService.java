package me.gabrielporto.comandapro.infrastructure.payment;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;

import me.gabrielporto.comandapro.shared.exception.BusinessException;

@Service
public class MercadoPagoPaymentService {

    public MpPaymentInfo fetchPayment(Long paymentId) {
        try {
            PaymentClient client = new PaymentClient();
            Payment payment = client.get(paymentId);

            return new MpPaymentInfo(
                    payment.getStatus(),
                    payment.getStatusDetail(),
                    payment.getExternalReference(),
                    payment.getMetadata()
            );
        } catch (MPApiException | MPException e) {
            throw new BusinessException("Erro ao buscar pagamento no Mercado Pago: " + e.getMessage());
        }
    }

    public record MpPaymentInfo(
            String status,
            String statusDetail,
            String externalReference,
            Map<String, Object> metadata
            ) {

    }
}

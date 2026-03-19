package me.gabrielporto.comandapro.core.services;

import me.gabrielporto.comandapro.core.domain.checkout.CheckoutTemporary;
import me.gabrielporto.comandapro.core.ports.in.CheckoutUseCase;
import me.gabrielporto.comandapro.core.ports.out.PaymentGatewayPort;

public class CheckoutService implements CheckoutUseCase {

    private final PaymentGatewayPort paymentGateway;

    public CheckoutService(PaymentGatewayPort paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    @Override
    public CheckoutTemporary startCheckout(CheckoutTemporary checkout) {
        // integrar criação de preferência de pagamento aqui
        return checkout;
    }

    @Override
    public void finalizeCheckout(String paymentId) {
        // lógica de finalização
    }
}

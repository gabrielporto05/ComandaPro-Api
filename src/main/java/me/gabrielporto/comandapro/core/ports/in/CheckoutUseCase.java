package me.gabrielporto.comandapro.core.ports.in;

import me.gabrielporto.comandapro.core.domain.checkout.CheckoutTemporary;

public interface CheckoutUseCase {
    CheckoutTemporary startCheckout(CheckoutTemporary checkout);
    void finalizeCheckout(String paymentId);
}

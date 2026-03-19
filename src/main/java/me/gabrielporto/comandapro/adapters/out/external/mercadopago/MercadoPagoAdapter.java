package me.gabrielporto.comandapro.adapters.out.external.mercadopago;

import me.gabrielporto.comandapro.core.domain.order.Order;
import me.gabrielporto.comandapro.core.ports.out.PaymentGatewayPort;

public class MercadoPagoAdapter implements PaymentGatewayPort {

    @Override
    public String createPreference(Order order) {
        // chamada ao SDK do Mercado Pago aqui
        return null;
    }
}

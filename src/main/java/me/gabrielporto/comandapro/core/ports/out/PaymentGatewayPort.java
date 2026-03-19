package me.gabrielporto.comandapro.core.ports.out;

import me.gabrielporto.comandapro.core.domain.order.Order;

public interface PaymentGatewayPort {

    String createPreference(Order order);
}

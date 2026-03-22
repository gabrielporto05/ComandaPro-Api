package me.gabrielporto.comandapro.infrastructure.web.websocket.dto;

import java.util.UUID;

import me.gabrielporto.comandapro.core.domain.order.OrderStatus;

public record OrderStatusNotification(UUID orderId, OrderStatus status, String orderCode) {

}

package me.gabrielporto.comandapro.infrastructure.web.websocket.dto;

import java.util.UUID;

import me.gabrielporto.comandapro.core.domain.order.OrderStatus;

public record StoreOrderNotification(String type, UUID orderId, OrderStatus status) {

}

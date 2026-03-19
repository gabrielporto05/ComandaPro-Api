package me.gabrielporto.comandapro.application.order;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.gabrielporto.comandapro.core.domain.order.Order;
import me.gabrielporto.comandapro.core.domain.order.OrderStatus;

@Service
public class UpdateOrderStatusUseCase {

    private final OrderService orderService;

    public UpdateOrderStatusUseCase(OrderService orderService) {
        this.orderService = orderService;
    }

    @Transactional
    public Order execute(UUID orderId, UUID userId, OrderStatus status, String cancellationReason) {
        return orderService.updateOrderStatus(orderId, userId, status, cancellationReason);
    }
}

package me.gabrielporto.comandapro.core.services;

import java.util.UUID;

import me.gabrielporto.comandapro.core.domain.order.Order;
import me.gabrielporto.comandapro.core.ports.out.OrderRepositoryPort;

public class OrderService {

    private final OrderRepositoryPort orderRepository;

    public OrderService(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order create(Order order) {
        return orderRepository.save(order);
    }

    public Order find(UUID id) {
        return orderRepository.findById(id).orElseThrow();
    }
}

package me.gabrielporto.comandapro.application.order;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.gabrielporto.comandapro.core.domain.order.Order;
import me.gabrielporto.comandapro.core.domain.order.OrderStatus;
import me.gabrielporto.comandapro.infrastructure.persistence.repository.OrderJpaRepository;
import me.gabrielporto.comandapro.infrastructure.persistence.repository.StoreJpaRepository;
import me.gabrielporto.comandapro.infrastructure.web.websocket.dto.OrderStatusNotification;
import me.gabrielporto.comandapro.infrastructure.web.websocket.dto.StoreOrderNotification;
import me.gabrielporto.comandapro.shared.exception.BusinessException;

@Service
public class OrderService {

    private final OrderJpaRepository orderRepository;
    private final StoreJpaRepository storeRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public OrderService(
            OrderJpaRepository orderRepository,
            StoreJpaRepository storeRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.orderRepository = orderRepository;
        this.storeRepository = storeRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional(readOnly = true)
    public Order getOrderPublic(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Pedido não encontrado"));
    }

    @Transactional(readOnly = true)
    public Order getOrderPublic(UUID storeId, String orderCode) {
        return orderRepository.findByStoreIdAndOrderCode(storeId, orderCode)
                .orElseThrow(() -> new BusinessException("Pedido não encontrado"));
    }

    @Transactional
    public Order cancelOrderByCustomer(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Pedido não encontrado"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Apenas pedidos pendentes podem ser cancelados pelo cliente");
        }

        order.cancel("Cancelado pelo cliente");
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelOrderByCustomer(UUID storeId, String orderCode) {
        Order order = getOrderPublic(storeId, orderCode);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Apenas pedidos pendentes podem ser cancelados pelo cliente");
        }

        order.cancel("Cancelado pelo cliente");
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Page<Order> listOrdersByStorePaginated(UUID storeId, UUID userId, int page, int size, String statusFilter) {
        validateStoreOwner(storeId, userId);

        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        if (statusFilter != null && !statusFilter.isBlank()) {
            try {
                OrderStatus status = OrderStatus.valueOf(statusFilter.trim().toUpperCase());
                return orderRepository.findByStoreIdAndStatus(storeId, status, pageable);
            } catch (IllegalArgumentException e) {
                return orderRepository.findByStoreId(storeId, pageable);
            }
        }
        return orderRepository.findByStoreId(storeId, pageable);
    }

    @Transactional(readOnly = true)
    public Order getOrder(UUID orderId, UUID userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Pedido não encontrado"));

        validateStoreOwner(order.getStore().getId(), userId);
        return order;
    }

    @Transactional
    public Order updateOrderStatus(UUID orderId, UUID userId, OrderStatus newStatus, String cancellationReason) {
        Order order = getOrder(orderId, userId);

        switch (newStatus) {
            case PREPARING ->
                order.accept();
            case READY ->
                order.markAsReady();
            case DELIVERED ->
                order.markAsDelivered();
            case CANCELLED ->
                order.cancel(cancellationReason != null ? cancellationReason : "Cancelado pela loja");
            default ->
                throw new BusinessException("Status inválido ou não permitido");
        }

        Order updatedOrder = orderRepository.save(order);

        notifyStatusUpdate(updatedOrder);

        return updatedOrder;
    }

    private void notifyStatusUpdate(Order order) {
        messagingTemplate.convertAndSend("/topic/order/" + order.getOrderCode(),
                new OrderStatusNotification(order.getId(), order.getStatus(), order.getOrderCode())
        );

        messagingTemplate.convertAndSend("/topic/store/" + order.getStore().getId(),
                new StoreOrderNotification("STATUS_UPDATE", order.getId(), order.getStatus())
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getOrderStats(UUID storeId, UUID userId, LocalDate date) {
        validateStoreOwner(storeId, userId);

        List<Order> ordersOfDay = orderRepository.findByStoreIdAndCreatedAtDate(storeId, date);

        long total = ordersOfDay.size();
        long pending = ordersOfDay.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count();
        long preparing = ordersOfDay.stream().filter(o -> o.getStatus() == OrderStatus.PREPARING).count();
        long ready = ordersOfDay.stream().filter(o -> o.getStatus() == OrderStatus.READY).count();
        long delivered = ordersOfDay.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count();
        long cancelled = ordersOfDay.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count();
        double totalAmount = ordersOfDay.stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .mapToDouble(order -> order.getTotalAmount() != null ? order.getTotalAmount() : 0.0)
                .sum();

        return Map.of(
                "date", date.toString(),
                "total", total,
                "pending", pending,
                "preparing", preparing,
                "ready", ready,
                "delivered", delivered,
                "cancelled", cancelled,
                "totalAmount", totalAmount
        );
    }

    @Transactional(readOnly = true)
    public List<Order> getRecentOrders(UUID storeId, UUID userId, int limit) {
        validateStoreOwner(storeId, userId);
        Pageable pageable = PageRequest.of(0, limit);
        return orderRepository.findByStoreIdOrderByCreatedAtDesc(storeId, pageable);
    }

    private void validateStoreOwner(UUID storeId, UUID userId) {
        var store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException("Loja não encontrada"));
        if (!store.getUser().getId().equals(userId)) {
            throw new BusinessException("Você não tem permissão para acessar pedidos desta loja");
        }
    }
}

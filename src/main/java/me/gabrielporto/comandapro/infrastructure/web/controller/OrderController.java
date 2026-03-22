package me.gabrielporto.comandapro.infrastructure.web.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import me.gabrielporto.comandapro.application.order.CreateOrderUseCase;
import me.gabrielporto.comandapro.application.order.OrderService;
import me.gabrielporto.comandapro.core.domain.order.Order;
import me.gabrielporto.comandapro.core.domain.order.OrderItem;
import me.gabrielporto.comandapro.core.domain.user.User;
import me.gabrielporto.comandapro.infrastructure.web.dto.request.CreateOrderRequest;
import me.gabrielporto.comandapro.infrastructure.web.dto.request.UpdateOrderStatusRequest;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.ApiResponse;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.OrderItemResponse;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.OrderResponse;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final OrderService orderService;

    public OrderController(CreateOrderUseCase createOrderUseCase, OrderService orderService) {
        this.createOrderUseCase = createOrderUseCase;
        this.orderService = orderService;
    }

    @PostMapping("/public/stores/{storeId}/orders")
    public ResponseEntity<ApiResponse> createOrder(
            @PathVariable UUID storeId,
            @Valid @RequestBody CreateOrderRequest request) {

        Order order = createOrderUseCase.execute(storeId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(
                        "Pedido realizado com sucesso",
                        toResponse(order)
                ));
    }

    @GetMapping("/public/orders/{orderId}")
    public ResponseEntity<ApiResponse> trackOrder(@PathVariable UUID orderId) {
        Order order = orderService.getOrderPublic(orderId);

        return ResponseEntity.ok(new ApiResponse(
                "Pedido encontrado",
                toResponse(order)
        ));
    }

    @GetMapping("/public/stores/{storeId}/orders/code/{orderCode}")
    public ResponseEntity<ApiResponse> trackOrderByCode(
            @PathVariable UUID storeId,
            @PathVariable String orderCode) {

        Order order = orderService.getOrderPublic(storeId, orderCode);

        return ResponseEntity.ok(new ApiResponse(
                "Pedido encontrado",
                toResponse(order)
        ));
    }

    @PatchMapping("/public/orders/{orderId}/cancel")
    public ResponseEntity<ApiResponse> cancelOrderByCustomer(@PathVariable UUID orderId) {
        Order order = orderService.cancelOrderByCustomer(orderId);

        return ResponseEntity.ok(new ApiResponse(
                "Pedido cancelado com sucesso",
                toResponse(order)
        ));
    }

    @PatchMapping("/public/stores/{storeId}/orders/code/{orderCode}/cancel")
    public ResponseEntity<ApiResponse> cancelOrderByCustomer(
            @PathVariable UUID storeId,
            @PathVariable String orderCode) {

        Order order = orderService.cancelOrderByCustomer(storeId, orderCode);

        return ResponseEntity.ok(new ApiResponse(
                "Pedido cancelado com sucesso",
                toResponse(order)
        ));
    }

    @GetMapping("/stores/{storeId}/orders")
    public ResponseEntity<ApiResponse> listOrders(
            @PathVariable UUID storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Page<Order> ordersPage = orderService.listOrdersByStorePaginated(storeId, user.getId(), page, size, status);

        List<OrderResponse> responses = ordersPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(new ApiResponse(
                "Pedidos listados com sucesso",
                new OrdersPageResponse(
                        responses,
                        ordersPage.getTotalElements(),
                        ordersPage.getTotalPages(),
                        ordersPage.getNumber()
                )
        ));
    }

    @GetMapping("/stores/{storeId}/orders/{orderId}")
    public ResponseEntity<ApiResponse> getOrder(
            @PathVariable UUID storeId,
            @PathVariable UUID orderId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Order order = orderService.getOrder(orderId, user.getId());

        return ResponseEntity.ok(new ApiResponse(
                "Pedido encontrado",
                toResponse(order)
        ));
    }

    @PatchMapping("/stores/{storeId}/orders/{orderId}/status")
    public ResponseEntity<ApiResponse> updateOrderStatus(
            @PathVariable UUID storeId,
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Order order = orderService.updateOrderStatus(
                orderId,
                user.getId(),
                request.status(),
                request.cancellationReason()
        );

        return ResponseEntity.ok(new ApiResponse(
                "Status do pedido atualizado",
                toResponse(order)
        ));
    }

    @GetMapping("/stores/{storeId}/orders/stats")
    public ResponseEntity<ApiResponse> getOrderStats(
            @PathVariable UUID storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        var stats = orderService.getOrderStats(storeId, user.getId(), date);

        return ResponseEntity.ok(new ApiResponse(
                "Estatísticas dos pedidos",
                stats
        ));
    }

    @GetMapping("/stores/{storeId}/orders/recent")
    public ResponseEntity<ApiResponse> getRecentOrders(
            @PathVariable UUID storeId,
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        List<Order> orders = orderService.getRecentOrders(storeId, user.getId(), limit);

        List<OrderResponse> responses = orders.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(new ApiResponse(
                "Pedidos recentes",
                responses
        ));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderCode(),
                order.getStore().getId(),
                order.getCustomerName(),
                order.getCustomerPhone(),
                order.getCustomerAddress(),
                order.getCustomerAddressNumber(),
                order.getCustomerAddressComplement(),
                order.getCustomerAddressNeighborhood(),
                order.getPaymentMethod().name(),
                order.getChangeFor(),
                order.getTotalAmount(),
                order.getDeliveryFee(),
                order.getGeneralObservation(),
                order.getStatus().name(),
                order.getPreparationTimeMinutes(),
                order.getAcceptedAt(),
                order.getReadyAt(),
                order.getDeliveredAt(),
                order.getCancelledAt(),
                order.getCancellationReason(),
                order.getCreatedAt(),
                items
        );
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice(),
                item.getObservation()
        );
    }

    private record OrdersPageResponse(
            List<OrderResponse> orders,
            long totalElements,
            int totalPages,
            int currentPage
            ) {

    }
}

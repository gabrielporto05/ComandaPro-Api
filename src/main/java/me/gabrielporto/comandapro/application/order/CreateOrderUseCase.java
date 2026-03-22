package me.gabrielporto.comandapro.application.order;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.gabrielporto.comandapro.core.domain.order.Order;
import me.gabrielporto.comandapro.core.domain.order.OrderItem;
import me.gabrielporto.comandapro.core.domain.order.OrderStatus;
import me.gabrielporto.comandapro.core.domain.store.Product;
import me.gabrielporto.comandapro.core.domain.store.Store;
import me.gabrielporto.comandapro.core.domain.store.StoreStatus;
import me.gabrielporto.comandapro.infrastructure.persistence.repository.OrderJpaRepository;
import me.gabrielporto.comandapro.infrastructure.persistence.repository.ProductJpaRepository;
import me.gabrielporto.comandapro.infrastructure.persistence.repository.StoreJpaRepository;
import me.gabrielporto.comandapro.infrastructure.web.dto.request.CreateOrderItemRequest;
import me.gabrielporto.comandapro.infrastructure.web.dto.request.CreateOrderRequest;
import me.gabrielporto.comandapro.infrastructure.web.websocket.dto.StoreOrderNotification;
import me.gabrielporto.comandapro.shared.exception.BusinessException;

@Service
public class CreateOrderUseCase {

    private final OrderJpaRepository orderRepository;
    private final StoreJpaRepository storeRepository;
    private final ProductJpaRepository productRepository;
    private final OrderCodeGenerator orderCodeGenerator;
    private final SimpMessagingTemplate messagingTemplate;

    public CreateOrderUseCase(
            OrderJpaRepository orderRepository,
            StoreJpaRepository storeRepository,
            ProductJpaRepository productRepository,
            OrderCodeGenerator orderCodeGenerator,
            SimpMessagingTemplate messagingTemplate) {
        this.orderRepository = orderRepository;
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
        this.orderCodeGenerator = orderCodeGenerator;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public Order execute(UUID storeId, CreateOrderRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException("Loja não encontrada"));

        if (store.getStatus() != StoreStatus.ACTIVE) {
            throw new BusinessException("Loja não está disponível para receber pedidos");
        }

        Double storeFee = store.getFeeDelivery();
        double deliveryFee = storeFee != null ? storeFee : 0.0;

        Order order = Order.builder()
                .store(store)
                .orderCode(orderCodeGenerator.generate(storeId))
                .customerName(request.customerName())
                .customerPhone(request.customerPhone())
                .customerAddress(request.customerAddress())
                .customerAddressNumber(request.customerAddressNumber())
                .customerAddressComplement(request.customerAddressComplement())
                .customerAddressNeighborhood(request.customerAddressNeighborhood())
                .paymentMethod(request.paymentMethod())
                .changeFor(request.changeFor())
                .deliveryFee(deliveryFee)
                .generalObservation(request.generalObservation())
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        for (CreateOrderItemRequest itemReq : request.items()) {
            Product product = productRepository.findById(itemReq.productId())
                    .orElseThrow(() -> new BusinessException("Produto não encontrado: " + itemReq.productId()));

            if (!product.getStore().getId().equals(storeId)) {
                throw new BusinessException("Produto não pertence a esta loja");
            }
            if (!Boolean.TRUE.equals(product.getIsAvailable())) {
                throw new BusinessException("Produto não disponível: " + product.getName());
            }

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .quantity(itemReq.quantity())
                    .unitPrice(product.getPrice())
                    .observation(itemReq.observation())
                    .build();

            order.addItem(item);
        }

        Order savedOrder = orderRepository.save(order);

        messagingTemplate.convertAndSend("/topic/store/" + storeId,
                new StoreOrderNotification("NEW_ORDER", savedOrder.getId(), savedOrder.getStatus())
        );

        return savedOrder;
    }
}

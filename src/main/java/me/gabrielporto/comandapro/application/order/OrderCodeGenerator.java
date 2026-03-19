package me.gabrielporto.comandapro.application.order;

import java.security.SecureRandom;
import java.util.UUID;

import org.springframework.stereotype.Component;

import me.gabrielporto.comandapro.infrastructure.persistence.repository.OrderJpaRepository;
import me.gabrielporto.comandapro.shared.exception.BusinessException;

@Component
public class OrderCodeGenerator {

    private static final int PREFERRED_LENGTH = 4;
    private static final int MAX_LENGTH = 6;
    private static final int MAX_ATTEMPTS = 50;

    private final SecureRandom random = new SecureRandom();
    private final OrderJpaRepository orderRepository;

    public OrderCodeGenerator(OrderJpaRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public String generate(UUID storeId) {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            int length = attempt < 25 ? PREFERRED_LENGTH : MAX_LENGTH;
            String code = randomNumeric(length);
            if (!orderRepository.existsByStoreIdAndOrderCode(storeId, code)) {
                return code;
            }
        }
        throw new BusinessException("Não foi possível gerar código do pedido. Tente novamente");
    }

    private String randomNumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}

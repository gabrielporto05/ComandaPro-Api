package me.gabrielporto.comandapro.application.checkout;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.gabrielporto.comandapro.core.domain.checkout.CheckoutTemporary;
import me.gabrielporto.comandapro.core.domain.store.Store;
import me.gabrielporto.comandapro.core.domain.store.StoreHours;
import me.gabrielporto.comandapro.core.domain.store.StoreStatus;
import me.gabrielporto.comandapro.core.domain.subscription.Subscription;
import me.gabrielporto.comandapro.core.domain.subscription.SubscriptionStatus;
import me.gabrielporto.comandapro.core.domain.user.User;
import me.gabrielporto.comandapro.core.domain.user.UserRole;
import me.gabrielporto.comandapro.core.domain.user.UserStatus;
import me.gabrielporto.comandapro.infrastructure.persistence.repository.CheckoutTemporaryRepository;
import me.gabrielporto.comandapro.infrastructure.persistence.repository.StoreJpaRepository;
import me.gabrielporto.comandapro.infrastructure.persistence.repository.SubscriptionJpaRepository;
import me.gabrielporto.comandapro.infrastructure.persistence.repository.UserJpaRepository;
import me.gabrielporto.comandapro.infrastructure.web.dto.request.CheckoutRequest;
import me.gabrielporto.comandapro.shared.exception.BusinessException;

@Service
public class CheckoutService {

    private final CheckoutTemporaryRepository checkoutRepository;
    private final UserJpaRepository userRepository;
    private final StoreJpaRepository storeRepository;
    private final SubscriptionJpaRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.checkout.expiration-seconds:3600}")
    private int expirationSeconds;

    public CheckoutService(
            CheckoutTemporaryRepository checkoutRepository,
            UserJpaRepository userRepository,
            StoreJpaRepository storeRepository,
            SubscriptionJpaRepository subscriptionRepository,
            PasswordEncoder passwordEncoder) {
        this.checkoutRepository = checkoutRepository;
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UUID iniciarCheckout(CheckoutRequest request) {
        if (userRepository.existsByEmail(request.client().email())) {
            throw new BusinessException("Email já cadastrado");
        }

        String slug = gerarSlug(request.store().store_name());

        CheckoutTemporary checkout = CheckoutTemporary.builder()
                .plan(request.plan().name())
                .expiresAt(LocalDateTime.now().plusSeconds(expirationSeconds))
                .client(CheckoutTemporary.ClientData.builder()
                        .name(request.client().name())
                        .email(request.client().email())
                        .tel(request.client().tel())
                        .passwordHash(passwordEncoder.encode(request.client().password()))
                        .build())
                .store(CheckoutTemporary.StoreData.builder()
                        .name(request.store().store_name())
                        .slug(slug)
                        .build())
                .processed(false)
                .build();

        checkout = checkoutRepository.save(checkout);
        return checkout.getId();
    }

    @Transactional
    public void processarPagamentoAprovado(UUID checkoutId) {
        CheckoutTemporary checkout = checkoutRepository.findByIdAndProcessedFalse(checkoutId)
                .orElseThrow(() -> new BusinessException("Checkout não encontrado ou já processado"));

        if (checkout.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Checkout expirado");
        }

        User user = User.builder()
                .name(checkout.getClient().getName())
                .email(checkout.getClient().getEmail())
                .tel(checkout.getClient().getTel())
                .password(checkout.getClient().getPasswordHash())
                .role(UserRole.OWNER)
                .status(UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);

        Store loja = Store.builder()
                .name(checkout.getStore().getName())
                .slug(checkout.getStore().getSlug())
                .user(user)
                .email(user.getEmail())
                .tel(user.getTel())
                .status(StoreStatus.ACTIVE)
                .build();

        loja = storeRepository.save(loja);

        criarHorariosPadrao(loja);

        Subscription assinatura = Subscription.builder()
                .store(loja)
                .plan(checkout.getPlan())
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(calcularFimAssinatura(checkout.getPlan()))
                .build();

        subscriptionRepository.save(assinatura);

        checkout.setProcessed(true);
        checkoutRepository.save(checkout);
    }

    private void criarHorariosPadrao(Store loja) {
        List<Object[]> horariosPadrao = Arrays.asList(
                new Object[]{0, false, "18:00", "23:00"},
                new Object[]{1, true, "18:00", "23:00"},
                new Object[]{2, true, "18:00", "23:00"},
                new Object[]{3, true, "18:00", "23:00"},
                new Object[]{4, true, "18:00", "23:00"},
                new Object[]{5, true, "18:00", "00:00"},
                new Object[]{6, true, "18:00", "00:00"}
        );

        for (Object[] horario : horariosPadrao) {
            StoreHours storeHours = StoreHours.builder()
                    .store(loja)
                    .dayOfWeek((Integer) horario[0])
                    .openTime((String) horario[2])
                    .closeTime((String) horario[3])
                    .isClosed(!(Boolean) horario[1])
                    .build();

            loja.getHorarios().add(storeHours);
        }
    }

    private String gerarSlug(String nome) {
        return nome.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
    }

    private LocalDateTime calcularFimAssinatura(String periodo) {
        return switch (periodo) {
            case "mensal" ->
                LocalDateTime.now().plusMonths(1);
            case "semestral" ->
                LocalDateTime.now().plusMonths(6);
            case "anual" ->
                LocalDateTime.now().plusYears(1);
            default ->
                LocalDateTime.now().plusMonths(1);
        };
    }

}

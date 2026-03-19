package me.gabrielporto.comandapro.infrastructure.web.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import me.gabrielporto.comandapro.application.store.CreateStoreUseCase;
import me.gabrielporto.comandapro.application.store.StoreService;
import me.gabrielporto.comandapro.application.store.UpdateStoreUseCase;
import me.gabrielporto.comandapro.core.domain.order.PaymentMethod;
import me.gabrielporto.comandapro.core.domain.store.Store;
import me.gabrielporto.comandapro.core.domain.store.StoreHours;
import me.gabrielporto.comandapro.core.domain.user.User;
import me.gabrielporto.comandapro.infrastructure.web.dto.request.CreateStoreHoursRequest;
import me.gabrielporto.comandapro.infrastructure.web.dto.request.CreateStoreRequest;
import me.gabrielporto.comandapro.infrastructure.web.dto.request.UpdateStoreRequest;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.ApiResponse;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.HoursResponse;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.StoreResponse;

@RestController
@RequestMapping("/api/v1")
public class StoreController {

    private final CreateStoreUseCase createStoreUseCase;
    private final UpdateStoreUseCase updateStoreUseCase;
    private final StoreService storeService;

    public StoreController(
            CreateStoreUseCase createStoreUseCase,
            UpdateStoreUseCase updateStoreUseCase,
            StoreService storeService) {
        this.createStoreUseCase = createStoreUseCase;
        this.updateStoreUseCase = updateStoreUseCase;
        this.storeService = storeService;
    }

    @GetMapping("/public/stores/{slug}")
    public ResponseEntity<ApiResponse> getStoreBySlug(@PathVariable String slug) {
        Store store = storeService.findBySlug(slug);

        return ResponseEntity.ok(new ApiResponse(
                "Loja encontrada",
                toResponse(store)
        ));
    }

    @GetMapping("/public/stores")
    public ResponseEntity<ApiResponse> listActiveStores() {
        List<Store> stores = storeService.findAllActive();

        List<StoreResponse> response = stores.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(new ApiResponse(
                "Lojas listadas com sucesso",
                response
        ));
    }

    @GetMapping("/public/stores/{slug}/hours")
    public ResponseEntity<ApiResponse> getStoreHours(@PathVariable String slug) {
        Store store = storeService.findBySlug(slug);

        List<HoursResponse> hours = store.getHorarios().stream()
                .map(this::toHoursResponse)
                .toList();

        return ResponseEntity.ok(new ApiResponse(
                "Horários encontrados",
                hours
        ));
    }

    @PutMapping("/stores/{storeId}/hours")
    public ResponseEntity<ApiResponse> updateAllStoreHours(
            @PathVariable UUID storeId,
            @Valid @RequestBody List<CreateStoreHoursRequest> requests,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        List<StoreHours> horariosAtualizados = storeService.updateAllHorarios(
                storeId,
                user.getId(),
                requests
        );

        List<HoursResponse> responses = horariosAtualizados.stream()
                .map(this::toHoursResponse)
                .toList();

        return ResponseEntity.ok(new ApiResponse(
                "Horários atualizados com sucesso",
                responses
        ));
    }

    @PostMapping("/stores")
    public ResponseEntity<ApiResponse> createStore(
            @Valid @RequestBody CreateStoreRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Store store = createStoreUseCase.execute(
                user.getId(),
                request.name(),
                request.email(),
                request.tel(),
                request.description(),
                request.address(),
                request.photos(),
                request.feeDelivery()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(
                        "Loja criada com sucesso",
                        toResponse(store)
                ));
    }

    @PutMapping("/stores/{storeId}")
    public ResponseEntity<ApiResponse> updateStore(
            @PathVariable UUID storeId,
            @Valid @RequestBody UpdateStoreRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Store store = updateStoreUseCase.execute(
                storeId,
                user.getId(),
                request.name(),
                request.slug(),
                request.email(),
                request.tel(),
                request.description(),
                request.address(),
                request.photos(),
                request.feeDelivery()
        );

        return ResponseEntity.ok(new ApiResponse(
                "Loja atualizada com sucesso",
                toResponse(store)
        ));
    }

    @GetMapping("/stores/me")
    public ResponseEntity<ApiResponse> getMyStore(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Store store = storeService.findByUserId(user.getId());

        return ResponseEntity.ok(new ApiResponse(
                "Loja encontrada",
                toResponse(store)
        ));
    }

    @PatchMapping("/stores/{storeId}/status")
    public ResponseEntity<ApiResponse> updateStoreStatus(
            @PathVariable UUID storeId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Store store = storeService.toggleStatus(storeId, user.getId());

        return ResponseEntity.ok(new ApiResponse(
                "Status da loja atualizado",
                toResponse(store)
        ));
    }

    @PostMapping("/stores/{storeId}/categories")
    public ResponseEntity<ApiResponse> addCategories(
            @PathVariable UUID storeId,
            @RequestBody List<String> categories,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Store store = storeService.addCategories(storeId, user.getId(), categories);

        return ResponseEntity.ok(new ApiResponse(
                "Categorias adicionadas com sucesso",
                toResponse(store)
        ));
    }

    @DeleteMapping("/stores/{storeId}/categories")
    public ResponseEntity<ApiResponse> removeCategories(
            @PathVariable UUID storeId,
            @RequestBody List<String> categories,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Store store = storeService.removeCategories(storeId, user.getId(), categories);

        return ResponseEntity.ok(new ApiResponse(
                "Categorias removidas com sucesso",
                toResponse(store)
        ));
    }

    @PostMapping("/stores/{storeId}/payment-methods")
    public ResponseEntity<ApiResponse> updatePaymentMethods(
            @PathVariable UUID storeId,
            @RequestBody List<PaymentMethod> paymentMethods,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Store store = storeService.updatePaymentMethods(storeId, user.getId(), paymentMethods);

        return ResponseEntity.ok(new ApiResponse(
                "Métodos de pagamento atualizados com sucesso",
                toResponse(store)
        ));
    }

    private StoreResponse toResponse(Store store) {
        return new StoreResponse(
                store.getId(),
                store.getName(),
                store.getSlug(),
                store.getUser().getId(),
                store.getEmail(),
                store.getTel(),
                store.getStatus().name(),
                store.getDescription(),
                store.getAddress(),
                store.getPhotos(),
                store.getFeeDelivery(),
                store.getCategories(),
                store.getPaymentMethods().stream()
                        .map(PaymentMethod::name)
                        .toList(),
                store.getCreatedAt(),
                store.getUpdatedAt(),
                store.getProducts().size(),
                store.getHorarios().stream()
                        .map(this::toHoursResponse)
                        .toList()
        );
    }

    private HoursResponse toHoursResponse(StoreHours horario) {
        return new HoursResponse(
                horario.getId(),
                horario.getDayOfWeek(),
                StoreHours.getDayName(horario.getDayOfWeek()),
                horario.getOpenTime(),
                horario.getCloseTime(),
                horario.getIsClosed(),
                horario.getFormattedTime()
        );
    }
}

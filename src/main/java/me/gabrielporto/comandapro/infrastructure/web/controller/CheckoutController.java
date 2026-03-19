package me.gabrielporto.comandapro.infrastructure.web.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import me.gabrielporto.comandapro.application.checkout.CheckoutService;
import me.gabrielporto.comandapro.infrastructure.payment.MercadoPagoPreferenceService;
import me.gabrielporto.comandapro.infrastructure.web.dto.request.CheckoutRequest;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.ApiResponse;
import me.gabrielporto.comandapro.infrastructure.web.dto.response.CheckoutResponse;

@RestController
@RequestMapping("/api/v1/public")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final MercadoPagoPreferenceService mercadoPagoPreferenceService;

    public CheckoutController(
            CheckoutService checkoutService,
            MercadoPagoPreferenceService mercadoPagoPreferenceService) {
        this.checkoutService = checkoutService;
        this.mercadoPagoPreferenceService = mercadoPagoPreferenceService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse> iniciarCheckout(@Valid @RequestBody CheckoutRequest request) {

        UUID checkoutId = checkoutService.iniciarCheckout(request);

        String checkoutUrl = mercadoPagoPreferenceService.createCheckoutUrl(request, checkoutId);

        CheckoutResponse response = new CheckoutResponse(
                checkoutId,
                checkoutUrl,
                3600
        );

        return ResponseEntity.ok(new ApiResponse(
                "Checkout iniciado com sucesso",
                response
        ));
    }
}

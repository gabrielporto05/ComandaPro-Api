package me.gabrielporto.comandapro.infrastructure.payment;

import com.mercadopago.MercadoPagoConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MercadoPagoConfigSetup {

    @Value("${mercadopago.access-token:}")
    private String accessToken;

    @PostConstruct
    public void init() {
        if (accessToken != null && !accessToken.isEmpty()) {
            MercadoPagoConfig.setAccessToken(accessToken);
            System.out.println("✅ Mercado Pago configurado com sucesso!");
        } else {
            System.err.println("⚠️ Access Token do Mercado Pago não configurado");
        }
    }
}

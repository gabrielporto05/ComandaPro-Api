package me.gabrielporto.comandapro.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.mercadopago.MercadoPagoConfig;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class MercadoPagoSetup {

    @Value("${mercadopago.access-token:}")
    private String accessToken;

    @PostConstruct
    public void setup() {
        if (accessToken != null && !accessToken.isBlank()) {
            MercadoPagoConfig.setAccessToken(accessToken);
            log.info("Mercado Pago configurado");
        } else {
            log.warn("Access token do Mercado Pago não definido");
        }
    }
}

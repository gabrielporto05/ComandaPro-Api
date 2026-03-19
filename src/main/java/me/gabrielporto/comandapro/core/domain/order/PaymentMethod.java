package me.gabrielporto.comandapro.core.domain.order;

import java.util.Arrays;
import java.util.Locale;

public enum PaymentMethod {
    DINHEIRO,
    PIX,
    CARTAO_CREDITO,
    CARTAO_DEBITO,
    VALE_REFEICAO;

    public static PaymentMethod parse(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT)
                .replace(" ", "_")
                .replace("-", "_")
                .replace("É", "E")
                .replace("Ê", "E");
        return Arrays.stream(values())
                .filter(pm -> pm.name().equals(normalized)
                || aliases(pm).contains(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported payment method: " + value));
    }

    private static java.util.List<String> aliases(PaymentMethod pm) {
        return switch (pm) {
            case DINHEIRO ->
                java.util.List.of("CASH", "MONEY");
            case PIX ->
                java.util.List.of("PIX");
            case CARTAO_CREDITO ->
                java.util.List.of("CARTAO_CREDITO", "CREDITO", "CREDIT_CARD");
            case CARTAO_DEBITO ->
                java.util.List.of("CARTAO_DEBITO", "DEBITO", "DEBIT_CARD");
            case VALE_REFEICAO ->
                java.util.List.of("VR", "VALE_REFEICAO", "VALE");
        };
    }
}

package me.gabrielporto.comandapro.core.domain.order;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PaymentMethod {
    DINHEIRO,
    PIX,
    CARTAO_CREDITO,
    CARTAO_DEBITO,
    VALE_REFEICAO;

    private static final Map<String, PaymentMethod> ALIAS_MAP = new HashMap<>();

    static {
        for (PaymentMethod method : values()) {
            ALIAS_MAP.put(method.name(), method);
        }

        ALIAS_MAP.put("CARTAO_DE_CREDITO", CARTAO_CREDITO);
        ALIAS_MAP.put("CARTAO_DEBITO", CARTAO_DEBITO);
        ALIAS_MAP.put("CARTAO_DE_DEBITO", CARTAO_DEBITO);
        ALIAS_MAP.put("CARTAO", CARTAO_CREDITO);
        ALIAS_MAP.put("VR", VALE_REFEICAO);
        ALIAS_MAP.put("VALE", VALE_REFEICAO);
    }

    @JsonCreator
    public static PaymentMethod fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        PaymentMethod method = tryByOrdinal(value);
        if (method != null) {
            return method;
        }

        String normalized = normalize(value);
        method = ALIAS_MAP.get(normalized);

        if (method == null) {
            throw new IllegalArgumentException("Forma de pagamento inválida: " + value);
        }

        return method;
    }

    private static PaymentMethod tryByOrdinal(String value) {
        try {
            int idx = Integer.parseInt(value);
            PaymentMethod[] methods = PaymentMethod.values();
            if (idx >= 0 && idx < methods.length) {
                return methods[idx];
            }
        } catch (NumberFormatException ignored) {
            // not a number, fall through
        }
        return null;
    }

    private static String normalize(String value) {
        String noAccents = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return noAccents
                .replaceAll("[^A-Za-z0-9]+", "_")
                .replaceAll("^_+|_+$", "")
                .toUpperCase();
    }
}

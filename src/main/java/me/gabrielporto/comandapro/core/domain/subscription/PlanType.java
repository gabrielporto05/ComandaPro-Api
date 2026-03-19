package me.gabrielporto.comandapro.core.domain.subscription;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PlanType {
    MENSAL,
    SEMESTRAL,
    ANUAL;

    @JsonCreator
    public static PlanType from(String value) {
        if (value == null) {
            return null;
        }
        return switch (value.trim().toUpperCase()) {
            case "MENSAL" ->
                MENSAL;
            case "SEMESTRAL" ->
                SEMESTRAL;
            case "ANUAL" ->
                ANUAL;
            default ->
                throw new IllegalArgumentException("Plano inválido: " + value);
        };
    }

    @JsonValue
    public String toJson() {
        return getPeriodo();
    }

    public String getPeriodo() {
        return switch (this) {
            case MENSAL ->
                "mensal";
            case SEMESTRAL ->
                "semestral";
            case ANUAL ->
                "anual";
        };
    }

    public double getPreco() {
        return switch (this) {
            case MENSAL ->
                29.90;
            case SEMESTRAL ->
                149.90;
            case ANUAL ->
                269.90;
        };
    }
}

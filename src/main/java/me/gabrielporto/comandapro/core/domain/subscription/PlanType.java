package me.gabrielporto.comandapro.core.domain.subscription;

import java.math.BigDecimal;

public enum PlanType {
    MENSAL(1, new BigDecimal("29.90")),
    SEMESTRAL(6, new BigDecimal("149.90")),
    ANUAL(12, new BigDecimal("269.90"));

    private final int months;
    private final BigDecimal price;

    PlanType(int months, BigDecimal price) {
        this.months = months;
        this.price = price;
    }

    public int getMonths() {
        return months;
    }

    public BigDecimal getPrice() {
        return price;
    }
}

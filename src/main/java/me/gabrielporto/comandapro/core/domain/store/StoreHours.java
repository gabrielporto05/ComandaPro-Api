package me.gabrielporto.comandapro.core.domain.store;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Domínio puro de horários da loja (sem JPA).
 */
public class StoreHours {

    private UUID id = UUID.randomUUID();
    private Store store;
    private Integer dayOfWeek; // 0 = Domingo
    private LocalTime openTime;
    private LocalTime closeTime;
    private boolean isClosed;
    private OffsetDateTime createdAt = OffsetDateTime.now();
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public StoreHours() {}

    public StoreHours(Store store, Integer dayOfWeek, LocalTime openTime, LocalTime closeTime, boolean isClosed) {
        this.store = store;
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.isClosed = isClosed;
    }

    public String getDayName() {
        if (dayOfWeek == null) return null;
        DayOfWeek dow = DayOfWeek.of(((dayOfWeek % 7) + 7) % 7 + 1); // 0->Domingo
        return dow.name();
    }

    public void setStore(Store store) { this.store = store; }
}

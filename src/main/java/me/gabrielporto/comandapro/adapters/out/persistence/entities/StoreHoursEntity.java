package me.gabrielporto.comandapro.adapters.out.persistence.entities;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "store_hours")
public class StoreHoursEntity {

    @Id
    @Column(nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "store_id")
    private StoreEntity store;

    @Column(nullable = false)
    private Integer dayOfWeek;

    private LocalTime openTime;
    private LocalTime closeTime;
    @Column(nullable = false)
    private boolean isClosed;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    // getters/setters
}

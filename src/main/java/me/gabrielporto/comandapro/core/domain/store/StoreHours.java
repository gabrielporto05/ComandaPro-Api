package me.gabrielporto.comandapro.core.domain.store;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "store_hours")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreHours {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false, columnDefinition = "UUID NOT NULL")
    private Store store;

    @Column(name = "day_of_week", nullable = false, columnDefinition = "INT NOT NULL")
    private Integer dayOfWeek;

    @Column(name = "open_time", columnDefinition = "VARCHAR(5)")
    private String openTime;

    @Column(name = "close_time", columnDefinition = "VARCHAR(5)")
    private String closeTime;

    @Column(name = "is_closed", nullable = false, columnDefinition = "BOOLEAN NOT NULL DEFAULT FALSE")
    private Boolean isClosed;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    public String getFormattedTime() {
        if (isClosed) {
            return "Fechado";
        }
        return openTime + " - " + closeTime;
    }

    public static String getDayName(int dayOfWeek) {
        return switch (dayOfWeek) {
            case 0 ->
                "Domingo";
            case 1 ->
                "Segunda";
            case 2 ->
                "Terça";
            case 3 ->
                "Quarta";
            case 4 ->
                "Quinta";
            case 5 ->
                "Sexta";
            case 6 ->
                "Sábado";
            default ->
                "Inválido";
        };
    }
}

package me.gabrielporto.comandapro.core.domain.store;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.gabrielporto.comandapro.core.domain.order.PaymentMethod;
import me.gabrielporto.comandapro.core.domain.order.PaymentMethodArrayConverter;
import me.gabrielporto.comandapro.core.domain.user.User;
import me.gabrielporto.comandapro.shared.exception.BusinessException;

@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID PRIMARY KEY")
    private UUID id;

    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @Column(name = "slug", nullable = false, unique = true, columnDefinition = "VARCHAR(255) NOT NULL UNIQUE")
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "UUID NOT NULL")
    private User user;

    @Column(name = "email", nullable = false, columnDefinition = "VARCHAR(255) NOT NULL")
    private String email;

    @Column(name = "tel", nullable = false, columnDefinition = "VARCHAR(255) NOT NULL")
    private String tel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(50) NOT NULL")
    private StoreStatus status;

    @Column(name = "description", length = 500, columnDefinition = "VARCHAR(500)")
    private String description;

    @Column(name = "address", columnDefinition = "VARCHAR(255)")
    private String address;

    @Column(name = "photos", columnDefinition = "TEXT[]")
    private List<String> photos;

    @Column(name = "fee_delivery", columnDefinition = "FLOAT")
    private Double feeDelivery;

    @Column(name = "categories", columnDefinition = "TEXT[]")
    private List<String> categories;

    @Convert(converter = PaymentMethodArrayConverter.class)
    @Column(name = "payment_methods", columnDefinition = "TEXT[]")
    private List<PaymentMethod> paymentMethods;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Product> products = new HashSet<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<StoreHours> horarios = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = StoreStatus.ACTIVE;
        }
        if (this.feeDelivery == null) {
            this.feeDelivery = 0.0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addProduct(Product product) {
        products.add(product);
        product.setStore(this);
    }

    public void removeProduct(Product product) {
        products.remove(product);
        product.setStore(null);
    }

    public void addHorario(StoreHours horario) {
        horarios.add(horario);
        horario.setStore(this);
    }

    public void removeHorario(StoreHours horario) {
        horarios.remove(horario);
        horario.setStore(null);
    }

    public void ativar() {
        if (this.status == StoreStatus.SUSPENDED) {
            this.status = StoreStatus.ACTIVE;
        } else {
            throw new BusinessException("Apenas lojas suspensas podem ser ativadas");
        }
    }

    public void suspender() {
        if (this.status == StoreStatus.ACTIVE) {
            this.status = StoreStatus.SUSPENDED;
        } else {
            throw new BusinessException("Apenas lojas ativas podem ser suspensas");
        }
    }

    public void cancelar() {
        if (this.status != StoreStatus.CANCELED) {
            this.status = StoreStatus.CANCELED;
        } else {
            throw new BusinessException("Loja já está cancelada");
        }
    }

}

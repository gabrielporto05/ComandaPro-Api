package me.gabrielporto.comandapro.core.domain.order;

public enum OrderStatus {
    PENDING, // Pendente
    PREPARING, // Em preparação
    READY, // Pronto para entrega
    DELIVERED, // Entregue
    CANCELLED   // Cancelado
}

package me.gabrielporto.comandapro.infrastructure.web.dto.request;

public record UpdateStoreRequest(
        String name,
        String slug,
        String email,
        String tel,
        String description,
        String address,
        String[] photos,
        Double feeDelivery
        ) {

}

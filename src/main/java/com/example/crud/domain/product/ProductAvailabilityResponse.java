package com.example.crud.domain.product;

public record ProductAvailabilityResponse(
        boolean available,
        String message
) {
    public static ProductAvailabilityResponse from(boolean available) {
        if (available) {
            return new ProductAvailabilityResponse(
                    true,
                    "Ótima notícia! Este produto está disponível para entrega no CEP informado."
            );
        }

        return new ProductAvailabilityResponse(
                false,
                "No momento, este produto ainda não está disponível para entrega no CEP informado. "
                        + "Você pode tentar outro CEP ou consultar novamente mais tarde."
        );
    }
}

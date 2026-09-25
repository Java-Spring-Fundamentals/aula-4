package com.example.crud.domain.product;

// Resposta do endpoint de disponibilidade.
// O campo principal é "available" (true/false); os outros ajudam a entender o resultado.
public record AvailabilityResponse(
        String productId,
        String cep,
        String cepCity,
        String distributionCenter,
        boolean available
) {
}

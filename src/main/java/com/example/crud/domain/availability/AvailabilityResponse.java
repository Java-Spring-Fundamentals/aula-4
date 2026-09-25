package com.example.crud.domain.availability;

public record AvailabilityResponse(
        String productId,
        String cep,
        String city,
        String distributionCenter,
        boolean available
) {
}

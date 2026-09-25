package com.atividade.produtos.dto;

public record AvailabilityResponse(
        String productId,
        String cep,
        String city,
        String distributionCenter,
        boolean available
) {
}
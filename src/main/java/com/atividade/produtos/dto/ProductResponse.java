package com.atividade.produtos.dto;

import com.atividade.produtos.model.Product;

import java.math.BigDecimal;

public record ProductResponse(
        String id,
        String name,
        String description,
        String category,
        BigDecimal price,
        boolean active,
        String distributionCenter
) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getPrice(),
                product.isActive(),
                product.getDistributionCenter()
        );
    }
}
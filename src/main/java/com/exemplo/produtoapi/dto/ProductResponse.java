package com.exemplo.produtoapi.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String category,
        BigDecimal price,
        Boolean active,
        String distributionCenter
) {
}

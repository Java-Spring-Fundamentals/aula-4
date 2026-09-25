package com.exemplo.produtoapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank(message = "O nome do produto e obrigatorio")
        String name,

        @NotBlank(message = "A categoria e obrigatoria")
        String category,

        @NotNull(message = "O preco e obrigatorio")
        @Positive(message = "O preco deve ser maior que zero")
        BigDecimal price,

        @NotBlank(message = "O centro de distribuicao e obrigatorio")
        @Pattern(regexp = "Mogi das Cruzes|Recife|Porto Alegre",
                message = "Centro de distribuicao deve ser Mogi das Cruzes, Recife ou Porto Alegre")
        String distributionCenter
) {
}

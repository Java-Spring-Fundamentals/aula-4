package com.atividade.produtos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String name,

        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
        String description,

        @NotBlank(message = "A categoria é obrigatória")
        @Size(max = 50, message = "A categoria deve ter no máximo 50 caracteres")
        String category,

        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser maior que zero")
        BigDecimal price,

        @NotBlank(message = "O centro de distribuição é obrigatório")
        @Pattern(regexp = "Mogi das Cruzes|Recife|Porto Alegre",
                message = "O centro de distribuição deve ser Mogi das Cruzes, Recife ou Porto Alegre")
        String distributionCenter
) {
}
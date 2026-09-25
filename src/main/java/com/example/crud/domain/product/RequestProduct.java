package com.example.crud.domain.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;

public record RequestProduct(
        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser maior que zero")
        Integer price,

        @NotBlank(message = "A categoria é obrigatória")
        String category,

        @NotBlank(message = "O centro de distribuição é obrigatório")
        @Pattern(
                regexp = "(?i)Mogi das Cruzes|Recife|Porto Alegre",
                message = "O centro de distribuição deve ser Mogi das Cruzes, Recife ou Porto Alegre"
        )
        String distributionCenter
) {
}

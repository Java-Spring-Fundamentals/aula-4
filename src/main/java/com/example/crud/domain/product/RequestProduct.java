package com.example.crud.domain.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RequestProduct(
        String id,

        @NotBlank
        String name,
        @NotNull
        Integer price,
        @NotBlank
        String category,
        @NotBlank
        @Pattern(
                regexp = "Mogi das Cruzes|Recife|Porto Alegre",
                message = "deve ser um dos centros de distribuição: Mogi das Cruzes, Recife ou Porto Alegre"
        )
        String distributionCenter
) {
}

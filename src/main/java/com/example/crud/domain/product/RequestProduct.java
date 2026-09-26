package com.example.crud.domain.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record RequestProduct (
        String id,

        @NotBlank(message = "Informe o nome do produto")
        String name,

        @NotNull(message = "Informe o preco")
        @Positive(message = "O preco deve ser maior que zero")
        Integer price,

        @NotBlank (message = "Informe a categoria")
        String category,

        @NotBlank(message = "Informe o centro de distribuicao")
        @Pattern(
                regexp = "Mogi das Cruzes|Recife|Porto Alegre",
                message = "Use Mogi das Cruzes, Recife ou Porto Alegre"
        )
        String distributionCenter
) {

}
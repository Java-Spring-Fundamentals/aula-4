package com.example.crud.domain.address;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ViaCepResponse(
    String localidade,
    Boolean erro
) {
    
}
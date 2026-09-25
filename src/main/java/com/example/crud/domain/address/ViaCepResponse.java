package com.example.crud.domain.address;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ViaCepResponse(
        String cep,
        String localidade,
        String uf,
        @JsonProperty("erro") Boolean error
) {
    public boolean notFound() {
        return Boolean.TRUE.equals(error);
    }
}

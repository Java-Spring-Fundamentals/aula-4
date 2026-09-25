package com.example.crud.domain.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ViaCepResponse {
    private String localidade;
    private String erro;

    @JsonCreator
    public ViaCepResponse(@JsonProperty("localidade") String localidade, @JsonProperty("erro") String erro) {
        this.localidade = localidade;
        this.erro = erro;
    }

    public String getLocalidade() {
        return localidade;
    }

    public String getErro() {
        return erro;
    }
}
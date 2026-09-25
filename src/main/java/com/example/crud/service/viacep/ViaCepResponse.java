package com.example.crud.service.viacep;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Representa o JSON retornado pela ViaCEP.
 * Ex.: https://viacep.com.br/ws/08773380/json/
 *
 * Quando o CEP tem formato válido mas não existe, a ViaCEP responde
 * HTTP 200 com o corpo {"erro": "true"} — por isso o campo "erro".
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ViaCepResponse(
        String cep,
        String logradouro,
        String bairro,
        String localidade,
        String uf,
        Boolean erro
) {
    public boolean notFound() {
        return Boolean.TRUE.equals(erro);
    }
}

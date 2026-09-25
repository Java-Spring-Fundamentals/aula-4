package com.exemplo.produtoapi.service;

import com.exemplo.produtoapi.dto.ViaCepResponse;
import com.exemplo.produtoapi.exception.CepInvalidoException;
import com.exemplo.produtoapi.exception.ViaCepIndisponivelException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
public class ViaCepService {

    private final RestClient restClient;

    public ViaCepService(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://viacep.com.br/ws").build();
    }

    public ViaCepResponse buscarEnderecoPorCep(String cep) {
        String cepLimpo = cep == null ? "" : cep.replaceAll("\\D", "");

        if (!cepLimpo.matches("\\d{8}")) {
            throw new CepInvalidoException("O CEP informado e invalido, use o formato com 8 digitos numericos");
        }

        try {
            ViaCepResponse resposta = restClient.get()
                    .uri("/{cep}/json/", cepLimpo)
                    .retrieve()
                    .body(ViaCepResponse.class);

            if (resposta == null || Boolean.TRUE.equals(resposta.erro())) {
                throw new CepInvalidoException("Nao foi encontrado nenhum endereco para o CEP " + cep);
            }

            return resposta;
        } catch (CepInvalidoException ex) {
            throw ex;
        } catch (RestClientException ex) {
            log.error("Falha ao consultar o ViaCEP para o CEP {}", cep, ex);
            throw new ViaCepIndisponivelException("O servico do ViaCEP esta indisponivel no momento, tente novamente em instantes");
        }
    }
}

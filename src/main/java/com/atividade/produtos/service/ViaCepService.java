package com.atividade.produtos.service;

import com.atividade.produtos.dto.ViaCepResponse;
import com.atividade.produtos.exception.CepNotFoundException;
import com.atividade.produtos.exception.InvalidCepException;
import com.atividade.produtos.exception.ViaCepUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class ViaCepService {

    private static final Logger log = LoggerFactory.getLogger(ViaCepService.class);

    private final RestClient restClient;

    public ViaCepService(@Value("${viacep.url}") String baseUrl,
                         @Value("${viacep.timeout-ms}") int timeoutMs) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeoutMs);
        requestFactory.setReadTimeout(timeoutMs);

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public String findCityByCep(String cep) {
        String cleanCep = normalizeCep(cep);

        ViaCepResponse response;
        try {
            response = restClient.get()
                    .uri("/ws/{cep}/json/", cleanCep)
                    .retrieve()
                    .body(ViaCepResponse.class);
        } catch (HttpClientErrorException ex) {
            throw new InvalidCepException(cep);
        } catch (RestClientException ex) {
            // 5xx, timeout, sem internet, resposta que nao e JSON...
            log.error("Falha ao consultar a ViaCEP para o CEP {}", cleanCep, ex);
            throw new ViaCepUnavailableException(ex);
        }

        if (response == null || Boolean.TRUE.equals(response.erro()) || response.localidade() == null) {
            throw new CepNotFoundException(cleanCep);
        }

        return response.localidade();
    }

    private String normalizeCep(String cep) {
        if (cep == null) {
            throw new InvalidCepException("");
        }
        String cleanCep = cep.trim().replace("-", "");
        if (!cleanCep.matches("\\d{8}")) {
            throw new InvalidCepException(cep);
        }
        return cleanCep;
    }
}
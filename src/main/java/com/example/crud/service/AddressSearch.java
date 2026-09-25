package com.example.crud.service;

import com.example.crud.domain.address.Address;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class AddressSearch {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AddressSearch(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public Address searchAddress(String cep) {

        String cepNumeros = cep.replaceAll("\\D", "");

        if (cepNumeros.length() != 8) {
            throw new IllegalArgumentException("CEP inválido");
        }

        String url = "https://viacep.com.br/ws/{cep}/json/";

        String responseBody;

        try {
            responseBody = restTemplate
                    .getForEntity(url, String.class, cepNumeros)
                    .getBody();

        } catch (RestClientException e) {
            throw new RuntimeException("ViaCEP indisponível", e);
        }

        try {
            JsonNode json = objectMapper.readTree(responseBody);

            if (json.has("erro") && json.get("erro").asBoolean()) {
                throw new IllegalArgumentException("CEP não encontrado");
            }

            return objectMapper.treeToValue(json, Address.class);

        } catch (IllegalArgumentException e) {
            throw e;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar o ViaCEP", e);
        }
    }
}
package com.example.crud.service;

import com.example.crud.domain.address.Address;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AddressSearch {

    private final RestTemplate restTemplate;

    public AddressSearch(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String searchAddress(String state, String city, String street) {
        if (state.isBlank() || city.isBlank() || street.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe estado, cidade e rua."
            );
        }

        Address[] enderecos;

        try {
            enderecos = restTemplate.getForObject(
                    "https://viacep.com.br/ws/{state}/{city}/{street}/json/",
                    Address[].class,
                    state,
                    city,
                    street
            );
        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Nao foi possivel consultar a ViaCEP."
            );
        }

        if (enderecos == null || enderecos.length == 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Nenhum CEP encontrado para esse endereco."
            );
        }

        if (enderecos[0] == null || enderecos[0].getCep() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "A ViaCEP retornou um endereco sem CEP."
            );
        }

        return enderecos[0].getCep();
    }
}
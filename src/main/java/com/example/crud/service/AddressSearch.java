package com.example.crud.service;

import com.example.crud.domain.address.Address;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;  

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AddressSearch {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AddressSearch(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String searchAddress(String state, String city, String street) {
        String url = "https://viacep.com.br/ws/{state}/{city}/{street}/json/";

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("state", state);
        uriVariables.put("city", city);
        uriVariables.put("street", street);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, uriVariables);

        try {
            List<Address> addresses = objectMapper.readValue(response.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, Address.class));
            String cep = addresses.get(0).getCep();
            return cep;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Address searchByCep(String cep) {
        if (cep == null || !cep.matches("[0-9]{8}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O CEP deve conter exatamente 8 números, sem hífen."
            );
        }

        String url = "https://viacep.com.br/ws/{cep}/json/";

        Address address;

        try {
            address = restTemplate.getForObject(
                    url,
                    Address.class,
                    cep
            );
        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Não foi possível consultar a ViaCEP. Tente novamente.",
                    exception
            );
        }

        if (address == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "A ViaCEP retornou uma resposta vazia."
            );
        }

        if (Boolean.TRUE.equals(address.getErro())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "CEP não encontrado."
            );
        }

        if (address.getLocalidade() == null
                || address.getLocalidade().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "A ViaCEP retornou uma resposta sem cidade."
            );
        }

        return address;
    }

    public boolean verifyAvailability(String cep, String distributionCenter) {
    Address address = searchByCep(cep);

    return distributionCenter.equals(address.getLocalidade());
    }
}

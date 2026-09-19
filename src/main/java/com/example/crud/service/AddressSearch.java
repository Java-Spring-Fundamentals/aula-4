package com.example.crud.service;

import com.example.crud.domain.address.Address;
import com.example.crud.infra.CepNotFoundException;
import com.example.crud.infra.InvalidCepException;
import com.example.crud.infra.ViaCepUnavailableException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public  Address searchByCep(String cep) {

        if (cep == null || !cep.matches("\\d{8}")){
            throw new InvalidCepException("O CEP  deve conter 8 dígitos.");
        }

        String url = "https://viacep.com.br/ws/{cep}/json/";

        try {
            Address address = restTemplate.getForObject(url, Address.class, cep);

            if (address == null || address.getLocalidade() == null) {
                throw new CepNotFoundException("CEP não encontrado.");
            }

            return  address;
        }catch (CepNotFoundException e){
            throw e;
        }catch (Exception e){
            throw new  ViaCepUnavailableException("O serviço ViaCEP está indisponível no momento. Por favor, tente novamente mais tarde.");
        }
    }
}

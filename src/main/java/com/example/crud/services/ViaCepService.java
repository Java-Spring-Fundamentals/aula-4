package com.example.crud.services;

import com.example.crud.domain.viacep.CepNotFoundException;
import com.example.crud.domain.viacep.InvalidCepException;
import com.example.crud.domain.viacep.ViaCepException;
import com.example.crud.domain.viacep.ViaCepResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class ViaCepService {

    private final RestClient restClient = RestClient.create("https://viacep.com.br");

    public boolean checkAvailability(String cep, String distributionCenter) {
        String city = findCityByCep(cep);
        return distributionCenter.trim().equalsIgnoreCase(city.trim());
    }

    public String findCityByCep(String cep) {
        String normalizedCep = validateCep(cep);
        ViaCepResponse response;

        try {
            response = restClient.get()
                    .uri("/ws/{cep}/json/", normalizedCep)
                    .retrieve()
                    .body(ViaCepResponse.class);
        } catch (RestClientException exception) {
            throw new ViaCepException();
        }

        if (response == null) {
            throw new ViaCepException();
        }

        if (Boolean.TRUE.equals(response.erro())) {
            throw new CepNotFoundException();
        }

        if (response.localidade() == null || response.localidade().isBlank()) {
            throw new ViaCepException();
        }

        return response.localidade();
    }

    private String validateCep(String cep) {
        if (cep == null || cep.isBlank()) {
            throw new InvalidCepException();
        }

        String normalizedCep = cep.replace("-", "");

        if (normalizedCep.length() != 8) {
            throw new InvalidCepException();
        }

        for (int i = 0; i < normalizedCep.length(); i++) {
            if (!Character.isDigit(normalizedCep.charAt(i))) {
                throw new InvalidCepException();
            }
        }

        return normalizedCep;
    }
}

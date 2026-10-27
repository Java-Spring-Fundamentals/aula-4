package com.example.crud.service;

import com.example.crud.domain.address.Address;
import com.example.crud.domain.product.Product;
import com.example.crud.infra.ViaCepException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class AvailabilityService {

    private static final String VIACEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    private final RestTemplate restTemplate;

    public AvailabilityService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isAvailable(Product product, String cep) {
        String city = findCityByCep(cep);
        return city.equalsIgnoreCase(product.getDistributionCenter());
    }

    private String findCityByCep(String cep) {
        String cleanCep = cep.replace("-", "");

        if (!cleanCep.matches("\\d{8}")) {
            throw new ViaCepException("Invalid CEP format", HttpStatus.BAD_REQUEST);
        }

        Address address;
        try {
            address = restTemplate.getForObject(VIACEP_URL, Address.class, cleanCep);
        } catch (RestClientException e) {
            throw new ViaCepException("ViaCEP service unavailable", HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (address == null || Boolean.TRUE.equals(address.getErro())) {
            throw new ViaCepException("CEP not found", HttpStatus.NOT_FOUND);
        }

        return address.getLocalidade();
    }
}
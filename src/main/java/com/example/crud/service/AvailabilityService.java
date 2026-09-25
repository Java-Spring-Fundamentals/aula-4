package com.example.crud.service;

import com.example.crud.domain.address.Address;
import com.example.crud.domain.product.Product;
import com.example.crud.domain.product.ProductRepository;
import com.example.crud.infra.ViaCepException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class AvailabilityService {

    private static final String VIACEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    private final RestTemplate restTemplate;
    private final ProductRepository repository;

    public AvailabilityService(RestTemplate restTemplate, ProductRepository repository) {
        this.restTemplate = restTemplate;
        this.repository = repository;
    }

    public boolean isAvailable(String productId, String cep) {
        Product product = repository.findById(productId).orElseThrow(EntityNotFoundException::new);
        String uf = findUf(cep);
        return product.getDistributionCenter() != null
                && product.getDistributionCenter().name().equalsIgnoreCase(uf);
    }

    public String findUf(String cep) {
        String cleanCep = cep == null ? "" : cep.replaceAll("[.\\-\\s]", "");
        if (!cleanCep.matches("\\d{8}")) {
            throw new ViaCepException("Invalid CEP format, it must have 8 digits", HttpStatus.BAD_REQUEST);
        }

        Address address;
        try {
            address = restTemplate.getForObject(VIACEP_URL, Address.class, cleanCep);
        } catch (RestClientException e) {
            throw new ViaCepException("ViaCEP service is unavailable, try again later", HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (address == null || Boolean.TRUE.equals(address.getErro()) || address.getUf() == null) {
            throw new ViaCepException("CEP not found", HttpStatus.NOT_FOUND);
        }
        return address.getUf();
    }
}

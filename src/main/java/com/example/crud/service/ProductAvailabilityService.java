package com.example.crud.service;

import com.example.crud.domain.product.Product;
import com.example.crud.domain.product.ProductRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductAvailabilityService {
    private final ProductRepository repository;
    private final RestTemplate restTemplate;

    public ProductAvailabilityService(ProductRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    public boolean isAvailable(String productId, String cep) {
        Product product = repository.findById(productId)
                .filter(p -> Boolean.TRUE.equals(p.getActive()))
                .orElseThrow(EntityNotFoundException::new);

        if (cep == null || !cep.matches("[0-9]{5}-?[0-9]{3}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CEP deve conter 8 dígitos");
        }

        try {
            ViaCepAddress address = restTemplate.getForObject(
                    "https://viacep.com.br/ws/{cep}/json/", ViaCepAddress.class,
                    cep.replace("-", ""));
            if (address == null || Boolean.TRUE.equals(address.erro()) || address.localidade() == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CEP não encontrado");
            }
            return address.localidade().trim().equalsIgnoreCase(product.getDistribution_center().trim());
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao consultar ViaCEP", ex);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ViaCepAddress(String localidade, Boolean erro) {}
}

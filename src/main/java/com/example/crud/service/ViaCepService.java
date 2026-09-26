package com.example.crud.service;

import com.example.crud.domain.address.ViaCepResponse;
import com.example.crud.domain.product.Product;
import com.example.crud.domain.product.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ViaCepService {

    private final ProductRepository repository;
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ViaCepService(
            ProductRepository repository,
            RestTemplate restTemplate,
            @Value("${viacep.base-url:https://viacep.com.br}") String baseUrl) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public boolean verificarDisponibilidade(String id, String cep) {
        if (cep == null || !cep.matches("[0-9]{8}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O CEP deve conter exatamente 8 numeros, sem hifen.");
        }

        Product product = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Produto nao encontrado."));

        String centro = product.getDistributionCenter();

        if (centro == null || centro.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Produto sem centro de distribuicao cadastrado.");
        }

        ViaCepResponse endereco;

        try {
            endereco = restTemplate.getForObject(
                    baseUrl + "/ws/{cep}/json/",
                    ViaCepResponse.class,
                    cep);
        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Nao foi possivel consultar a ViaCEP. Tente novamente mais tarde.");
        }

        if (endereco == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "A ViaCEP retornou uma resposta vazia.");
        }

        if (Boolean.TRUE.equals(endereco.erro())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "CEP nao encontrado.");
        }

        if (endereco.localidade() == null || endereco.localidade().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "A ViaCEP retornou um endereco sem cidade.");
        }

        return centro.trim().equalsIgnoreCase(endereco.localidade().trim());
    }
}
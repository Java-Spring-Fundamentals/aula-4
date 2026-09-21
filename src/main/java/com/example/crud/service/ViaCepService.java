package com.example.crud.service;

import com.example.crud.domain.product.Product;
import com.example.crud.domain.product.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ViaCepService {

    private final RestTemplate restTemplate;
    private final ProductRepository productRepository;

    public ViaCepService(RestTemplate restTemplate, ProductRepository productRepository) {
        this.restTemplate = restTemplate;
        this.productRepository = productRepository;
    }

    public boolean checkAvailability(String productId, String cep) {
        Product product = productRepository.findById(productId)
                .orElseThrow(EntityNotFoundException::new);

        String normalizedCep = normalizeCep(cep);

        try {
            String url = "https://viacep.com.br/ws/{cep}/json/";
            ResponseEntity<ViaCepResponse> response =
                    restTemplate.getForEntity(url, ViaCepResponse.class, normalizedCep);

            ViaCepResponse address = response.getBody();

            if (!response.getStatusCode().is2xxSuccessful()
                    || address == null
                    || Boolean.TRUE.equals(address.erro())) {
                throw new ViaCepException("CEP não encontrado na ViaCEP.");
            }

            String city = address.localidade();
            String distributionCenter = product.getDistributionCenter();

            return city != null
                    && distributionCenter != null
                    && city.trim().equalsIgnoreCase(distributionCenter.trim());

        } catch (ViaCepException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ViaCepException("Não foi possível consultar a ViaCEP no momento.");
        }
    }

    private String normalizeCep(String cep) {
        if (cep == null) {
            throw new ViaCepException("CEP deve ser informado.");
        }

        String normalized = cep.replaceAll("\\D", "");

        if (!normalized.matches("\\d{8}")) {
            throw new ViaCepException("CEP inválido. Informe 8 dígitos.");

        }

        return normalized;
    }

    private record ViaCepResponse(
            String cep,
            String localidade,
            String uf,
            Boolean erro
    ) {
    }
}

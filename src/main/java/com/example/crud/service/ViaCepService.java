package com.example.crud.services;

import com.example.crud.domain.product.DistributionCenter;
import com.example.crud.domain.product.Product;
import com.example.crud.domain.product.ProductRepository;
import com.example.crud.domain.viacep.ViaCepResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ViaCepService {

    private static final String VIACEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    @Autowired
    private ProductRepository productRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean isProductAvailable(String productId, String cep) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

        if (Boolean.FALSE.equals(product.getActive())) {
            return false;
        }

        ViaCepResponse viaCepResponse = consultCep(cep);

        if (viaCepResponse == null || viaCepResponse.isErro() || viaCepResponse.getUf() == null) {
            return false; 
        }

        DistributionCenter productCenter = product.getDistributionCenter();
        String ufFromCep = viaCepResponse.getUf().toUpperCase();

        return productCenter != null && productCenter.name().equals(ufFromCep);
    }

    public ViaCepResponse consultCep(String cep) {
        if (cep == null || cep.isBlank()) {
            return null;
        }

        String cleanCep = cep.replaceAll("\\D", "");

        if (cleanCep.length() != 8) {
            return null;
        }

        try {
            return restTemplate.getForObject(VIACEP_URL, ViaCepResponse.class, cleanCep);
        } catch (RestClientException e) {
            return null; 
        }
    }
}
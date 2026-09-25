package com.example.crud.service;

import com.example.crud.domain.product.AvailabilityResponse;
import com.example.crud.domain.product.Product;
import com.example.crud.domain.product.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.text.Normalizer;

// Regra de negócio: um produto está disponível para um CEP quando a cidade
// desse CEP é a mesma cidade do centro de distribuição do produto.
@Service
public class AvailabilityService {

    private final ProductRepository repository;
    private final ViaCepService viaCepService;

    public AvailabilityService(ProductRepository repository, ViaCepService viaCepService) {
        this.repository = repository;
        this.viaCepService = viaCepService;
    }

    public AvailabilityResponse checkAvailability(String productId, String cep) {
        // 1. Busca o produto. Inexistente ou inativado -> 404 (não adianta consultar a ViaCEP).
        Product product = repository.findById(productId)
                .filter(p -> Boolean.TRUE.equals(p.getActive()))
                .orElseThrow(EntityNotFoundException::new);

        // 2. Descobre a cidade do CEP na ViaCEP
        String cepCity = viaCepService.findCityByCep(cep);

        // 3. Compara as duas cidades
        boolean available = isSameCity(cepCity, product.getDistributionCenter());

        return new AvailabilityResponse(product.getId(), cep, cepCity, product.getDistributionCenter(), available);
    }

    // Compara ignorando maiúsculas/minúsculas, acentos e espaços nas pontas.
    // Assim "São Paulo", "sao paulo" e " SAO PAULO " são considerados iguais.
    static boolean isSameCity(String city, String distributionCenter) {
        if (city == null || distributionCenter == null) {
            return false;
        }
        return normalize(city).equals(normalize(distributionCenter));
    }

    private static String normalize(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "") // remove os acentos
                .trim()
                .toLowerCase();
    }
}

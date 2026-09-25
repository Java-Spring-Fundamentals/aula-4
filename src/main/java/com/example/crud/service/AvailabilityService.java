package com.example.crud.service;

import com.example.crud.domain.availability.AvailabilityResponse;
import com.example.crud.domain.product.Product;
import com.example.crud.domain.product.ProductRepository;
import com.example.crud.service.viacep.ViaCepResponse;
import com.example.crud.service.viacep.ViaCepService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Verifica se um produto está disponível na cidade do CEP informado,
 * comparando a cidade retornada pela ViaCEP com o distributionCenter do produto.
 */
@Service
public class AvailabilityService {

    private final ProductRepository repository;
    private final ViaCepService viaCepService;

    public AvailabilityService(ProductRepository repository, ViaCepService viaCepService) {
        this.repository = repository;
        this.viaCepService = viaCepService;
    }

    public AvailabilityResponse checkAvailability(String productId, String cep) {
        // 1. Busca o produto primeiro: se não existir, nem chama a API externa
        Product product = repository.findById(productId)
                .filter(p -> Boolean.TRUE.equals(p.getActive()))
                .orElseThrow(EntityNotFoundException::new);

        // 2. Descobre a cidade do CEP
        ViaCepResponse address = viaCepService.findAddressByCep(cep);

        // 3. Compara
        boolean available = isSameCity(address.localidade(), product.getDistributionCenter());

        return new AvailabilityResponse(
                product.getId(),
                address.cep(),
                address.localidade(),
                product.getDistributionCenter(),
                available
        );
    }

    /**
     * Retorna true se as cidades forem iguais, ignorando maiúsculas/minúsculas,
     * acentos e espaços extras ("São Paulo" == "sao paulo").
     */
    public boolean isSameCity(String city, String distributionCenter) {
        if (city == null || distributionCenter == null) {
            return false;
        }
        return normalize(city).equals(normalize(distributionCenter));
    }

    private String normalize(String value) {
        String withoutAccents = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return withoutAccents.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}

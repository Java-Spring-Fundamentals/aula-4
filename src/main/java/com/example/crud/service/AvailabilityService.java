package com.example.crud.service;

import com.example.crud.domain.address.Address;
import com.example.crud.domain.product.DistributionCenter;
import com.example.crud.domain.product.Product;
import com.example.crud.domain.product.ProductRepository;
import com.example.crud.infra.CepNotFoundException;
import com.example.crud.infra.ExternalServiceException;
import com.example.crud.infra.InvalidCepException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.text.Normalizer;

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
        Product product = repository.findByIdAndActiveTrue(productId)
                .orElseThrow(EntityNotFoundException::new);

        Address address = searchAddress(cep);
        DistributionCenter center = product.getDistributionCenter();

        boolean sameCity = normalize(address.getLocalidade()).equals(normalize(center.getCity()));
        boolean sameState = center.getUf().equalsIgnoreCase(address.getUf());
        return sameCity && sameState;
    }

    private Address searchAddress(String cep) {
        if (cep == null || !cep.trim().matches("\\d{5}-?\\d{3}")) {
            throw new InvalidCepException("CEP must have 8 digits");
        }
        String cleanCep = cep.trim().replace("-", "");

        Address address;
        try {
            address = restTemplate.getForObject(VIACEP_URL, Address.class, cleanCep);
        } catch (HttpClientErrorException e) {
            throw new InvalidCepException("Invalid CEP");
        } catch (RestClientException e) {
            throw new ExternalServiceException("ViaCEP service is unavailable, try again later");
        }

        if (address == null || Boolean.TRUE.equals(address.getErro()) || address.getLocalidade() == null) {
            throw new CepNotFoundException("CEP not found");
        }
        return address;
    }

    private String normalize(String text) {
        if (text == null) {
            return "";
        }
        String noAccents = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return noAccents.trim().toLowerCase();
    }
}
package com.example.crud.service;

import com.example.crud.domain.address.ViaCepResponse;
import com.example.crud.domain.product.Product;
import com.example.crud.infra.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.text.Normalizer;
import java.util.Locale;

@Service
public class ViaCepService {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final ProductService productService;

    public ViaCepService(
            RestTemplate restTemplate,
            @Value("${viacep.base-url}") String baseUrl,
            ProductService productService
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.productService = productService;
    }

    public ViaCepResponse findAddress(String cep) {
        String normalizedCep = normalizeCep(cep);

        try {
            var response = restTemplate.getForEntity(
                    baseUrl + "/{cep}/json/",
                    ViaCepResponse.class,
                    normalizedCep
            );

            HttpStatusCode status = response.getStatusCode();
            ViaCepResponse address = response.getBody();

            if (!status.is2xxSuccessful() || address == null) {
                throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "A ViaCEP retornou uma resposta inválida");
            }
            if (address.notFound()) {
                throw new ApiException(HttpStatus.NOT_FOUND, "CEP não encontrado: " + normalizedCep);
            }
            if (address.localidade() == null || address.localidade().isBlank()) {
                throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "A ViaCEP não informou a cidade do CEP");
            }

            return address;
        } catch (ApiException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new ApiException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Não foi possível consultar a ViaCEP",
                    exception
            );
        }
    }

    public boolean isAvailable(String productId, String cep) {
        Product product = productService.findActiveById(productId);
        String city = findAddress(cep).localidade();
        return normalize(city).equals(normalize(product.getDistributionCenter()));
    }

    private String normalizeCep(String cep) {
        if (cep == null || !cep.trim().matches("\\d{5}-?\\d{3}")) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "O CEP deve conter oito dígitos, com hífen opcional"
            );
        }
        return cep.replace("-", "");
    }

    private String normalize(String value) {
        String withoutAccents = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return withoutAccents.trim().toLowerCase(Locale.ROOT);
    }
}

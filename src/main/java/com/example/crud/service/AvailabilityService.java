package com.example.crud.service;

import com.example.crud.domain.address.Address;
import com.example.crud.domain.product.Product;
import com.example.crud.service.exceptions.CepNotFoundException;
import com.example.crud.service.exceptions.InvalidCepException;
import com.example.crud.service.exceptions.ViaCepUnavailableException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Pattern;

@Service
public class AvailabilityService {

    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/{cep}/json/";
    private static final Pattern CEP_PATTERN = Pattern.compile("^\\d{8}$");

    private final RestTemplate restTemplate;

    public AvailabilityService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isProductAvailableInCep(String cep, Product product) {
        String city = findCityByCep(cep);
        return city.equalsIgnoreCase(product.getDistributionCenter());
    }

    private String findCityByCep(String cep) {
        String sanitizedCep = cep == null ? "" : cep.replaceAll("[^0-9]", "");

        if (!CEP_PATTERN.matcher(sanitizedCep).matches()) {
            throw new InvalidCepException("Invalid CEP: " + cep);
        }

        Address address;
        try {
            address = restTemplate.getForObject(VIA_CEP_URL, Address.class, sanitizedCep);
        } catch (HttpClientErrorException.BadRequest e) {
            throw new InvalidCepException("Invalid CEP: " + cep);
        } catch (RestClientException e) {
            throw new ViaCepUnavailableException("ViaCEP service is currently unavailable");
        }

        if (address == null || Boolean.TRUE.equals(address.getErro()) || address.getLocalidade() == null) {
            throw new CepNotFoundException("CEP not found: " + cep);
        }

        return address.getLocalidade();
    }
}

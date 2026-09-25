package com.example.crud.service;

import com.example.crud.domain.product.ViaCepResponse;
import com.example.crud.infra.CepNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ViaCepService {

    private final RestTemplate restTemplate;

    public ViaCepService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isProductAvailable(String cep, String distributionCenter) {
        String url = "https://viacep.com.br/ws/{cep}/json/";
        ViaCepResponse response = restTemplate.getForObject(url, ViaCepResponse.class, cep);

        if (response == null || response.getErro() != null) {
            throw new CepNaoEncontradoException("CEP não encontrado: " + cep);
        }

        return distributionCenter.equals(response.getLocalidade());
    }
}
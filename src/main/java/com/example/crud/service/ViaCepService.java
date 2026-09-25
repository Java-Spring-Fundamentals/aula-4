package com.example.crud.service;

import com.example.crud.domain.address.Address;
import com.example.crud.infra.ViaCepIndisponivelException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ViaCepService {

    private final RestTemplate restTemplate;

    public ViaCepService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Address buscarPorCep(String cep) {
        String limpo = cep == null ? "" : cep.replace("-", "").replace(".", "").trim();

        if (limpo.length() != 8) {
            throw new IllegalArgumentException("CEP inválido, precisa ter 8 números");
        }

        String url = "https://viacep.com.br/ws/" + limpo + "/json/";

        try {
            Address endereco = restTemplate.getForObject(url, Address.class);

            if (endereco == null || Boolean.TRUE.equals(endereco.getErro()) || endereco.getLocalidade() == null) {
                throw new IllegalArgumentException("CEP não encontrado");
            }

            return endereco;
        } catch (RestClientException e) {
            throw new ViaCepIndisponivelException("ViaCEP fora do ar no momento");
        }
    }

    public boolean cidadeBateComCentro(String distributionCenter, String cep) {
        Address endereco = buscarPorCep(cep);

        if (distributionCenter == null) {
            return false;
        }

        return distributionCenter.equalsIgnoreCase(endereco.getLocalidade());
    }
}

package com.example.crud.service;

import com.example.crud.domain.address.Address;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AvailabilityService {

    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    private final RestTemplate restTemplate;

    public AvailabilityService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Consulta a ViaCEP e devolve a cidade do CEP. Devolve null se o CEP nao existir.
    public String searchCityByCep(String cep) {
        Address address = restTemplate.getForObject(VIA_CEP_URL, Address.class, cep);

        if (address == null || address.getLocalidade() == null) {
            return null;
        }
        return address.getLocalidade();
    }

    // Compara a cidade do CEP com o centro de distribuicao do produto.
    public boolean isSameCity(String city, String distributionCenter) {
        if (city == null || distributionCenter == null) {
            return false;
        }
        return city.trim().equalsIgnoreCase(distributionCenter.trim());
    }
}

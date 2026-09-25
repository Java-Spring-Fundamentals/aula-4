package com.example.crud.service;

import com.example.crud.domain.address.Address;
import com.example.crud.infra.exceptions.CepNotFoundException;
import com.example.crud.infra.exceptions.InvalidCepException;
import com.example.crud.infra.exceptions.ViaCepUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Pattern;

// Única classe do sistema que conversa com a ViaCEP.
// Recebe um CEP e devolve o nome da cidade, ou lança uma exceção que explica o problema.
@Service
public class ViaCepService {

    // 5 dígitos, hífen opcional, 3 dígitos. Aceita "08773380" e "08773-380".
    private static final Pattern CEP_FORMAT = Pattern.compile("^\\d{5}-?\\d{3}$");

    private final RestTemplate restTemplate;
    private final String viaCepUrl;

    public ViaCepService(RestTemplate restTemplate, @Value("${viacep.url}") String viaCepUrl) {
        this.restTemplate = restTemplate;
        this.viaCepUrl = viaCepUrl;
    }

    public String findCityByCep(String cep) {
        String cleanCep = normalizeCep(cep);

        Address address;
        try {
            // Monta a URL https://viacep.com.br/ws/{cep}/json/ e converte o JSON em Address
            address = restTemplate.getForObject(viaCepUrl + "/{cep}/json/", Address.class, cleanCep);
        } catch (HttpClientErrorException.BadRequest e) {
            // A ViaCEP responde 400 quando o CEP é mal formatado
            throw new InvalidCepException(cep);
        } catch (RestClientException e) {
            // Qualquer outra falha: timeout, sem conexão, erro 5xx da ViaCEP, JSON inesperado...
            throw new ViaCepUnavailableException(e);
        }

        // CEP bem formatado mas inexistente: a ViaCEP responde 200 com {"erro": "true"}
        if (address == null || Boolean.TRUE.equals(address.getErro())
                || address.getLocalidade() == null || address.getLocalidade().isBlank()) {
            throw new CepNotFoundException(cleanCep);
        }

        return address.getLocalidade();
    }

    // Valida o formato ANTES de chamar a API (evita uma chamada inútil pela internet)
    // e remove o hífen, deixando só os 8 dígitos.
    static String normalizeCep(String cep) {
        if (cep == null || !CEP_FORMAT.matcher(cep.trim()).matches()) {
            throw new InvalidCepException(cep);
        }
        return cep.trim().replace("-", "");
    }
}

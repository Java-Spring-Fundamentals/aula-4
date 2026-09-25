package com.example.crud.service.viacep;

import com.example.crud.infra.exceptions.CepNotFoundException;
import com.example.crud.infra.exceptions.InvalidCepException;
import com.example.crud.infra.exceptions.ViaCepUnavailableException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Serviço responsável por toda a comunicação com a API externa ViaCEP.
 * Traduz as falhas externas (timeout, erro 5xx, CEP inválido/inexistente)
 * em exceções da própria aplicação, tratadas no RequestsExceptionHandler.
 */
@Service
public class ViaCepService {

    private static final String VIACEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    private final RestTemplate restTemplate;

    public ViaCepService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ViaCepResponse findAddressByCep(String cep) {
        String normalizedCep = normalizeCep(cep);

        ViaCepResponse response;
        try {
            response = restTemplate.getForObject(VIACEP_URL, ViaCepResponse.class, normalizedCep);
        } catch (HttpClientErrorException e) {
            // 4xx: a ViaCEP devolve 400 quando o formato do CEP é inválido
            throw new InvalidCepException(cep);
        } catch (HttpServerErrorException | ResourceAccessException e) {
            // 5xx, timeout, sem conexão, DNS etc.
            throw new ViaCepUnavailableException(e);
        } catch (RestClientException e) {
            // qualquer outra falha (ex.: resposta que não é JSON)
            throw new ViaCepUnavailableException(e);
        }

        if (response == null) {
            throw new ViaCepUnavailableException(null);
        }
        if (response.notFound() || response.localidade() == null) {
            throw new CepNotFoundException(normalizedCep);
        }
        return response;
    }

    /**
     * Aceita "08773-380", "08773380" ou " 08773380 " e devolve só os 8 dígitos.
     * Valida antes de chamar a API para não gastar uma requisição à toa.
     */
    String normalizeCep(String cep) {
        if (cep == null) {
            throw new InvalidCepException(null);
        }
        String trimmed = cep.trim();
        if (!trimmed.matches("\\d{5}-?\\d{3}")) {
            throw new InvalidCepException(cep);
        }
        return trimmed.replace("-", "");
    }
}

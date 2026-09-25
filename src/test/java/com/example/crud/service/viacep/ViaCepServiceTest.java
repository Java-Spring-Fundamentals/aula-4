package com.example.crud.service.viacep;

import com.example.crud.infra.exceptions.CepNotFoundException;
import com.example.crud.infra.exceptions.InvalidCepException;
import com.example.crud.infra.exceptions.ViaCepUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class ViaCepServiceTest {

    private static final String URL = "https://viacep.com.br/ws/08773380/json/";

    private MockRestServiceServer server;
    private ViaCepService service;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        service = new ViaCepService(restTemplate);
    }

    @Test
    void shouldReturnAddressWhenCepExists() {
        server.expect(requestTo(URL)).andRespond(withSuccess(
                "{\"cep\":\"08773-380\",\"localidade\":\"Mogi das Cruzes\",\"uf\":\"SP\"}",
                MediaType.APPLICATION_JSON));

        ViaCepResponse response = service.findAddressByCep("08773-380");

        assertEquals("Mogi das Cruzes", response.localidade());
        server.verify();
    }

    @Test
    void shouldThrowNotFoundWhenViaCepReturnsErro() {
        server.expect(requestTo("https://viacep.com.br/ws/99999999/json/"))
                .andRespond(withSuccess("{\"erro\": \"true\"}", MediaType.APPLICATION_JSON));

        assertThrows(CepNotFoundException.class, () -> service.findAddressByCep("99999999"));
    }

    @Test
    void shouldRejectMalformedCepWithoutCallingApi() {
        assertThrows(InvalidCepException.class, () -> service.findAddressByCep("123"));
        assertThrows(InvalidCepException.class, () -> service.findAddressByCep("abcde-fgh"));
        assertThrows(InvalidCepException.class, () -> service.findAddressByCep(null));
        server.verify(); // nenhuma chamada foi feita
    }

    @Test
    void shouldThrowUnavailableWhenViaCepIsDown() {
        server.expect(requestTo(URL)).andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertThrows(ViaCepUnavailableException.class, () -> service.findAddressByCep("08773380"));
    }

    @Test
    void shouldThrowUnavailableOnTimeout() {
        server.expect(requestTo(URL)).andRespond(withException(new SocketTimeoutException("timeout")));

        assertThrows(ViaCepUnavailableException.class, () -> service.findAddressByCep("08773380"));
    }
}

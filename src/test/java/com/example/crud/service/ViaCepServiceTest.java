package com.example.crud.service;

import com.example.crud.domain.product.Product;
import com.example.crud.infra.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ViaCepServiceTest {

    private MockRestServiceServer server;
    private ViaCepService service;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        productService = mock(ProductService.class);
        service = new ViaCepService(restTemplate, "https://viacep.com.br/ws", productService);
    }

    @Test
    void shouldReturnTheCityForAValidCep() {
        server.expect(once(), requestTo("https://viacep.com.br/ws/08773380/json/"))
                .andRespond(withSuccess(
                        "{\"cep\":\"08773-380\",\"localidade\":\"Mogi das Cruzes\",\"uf\":\"SP\"}",
                        MediaType.APPLICATION_JSON
                ));

        var response = service.findAddress("08773-380");

        assertEquals("Mogi das Cruzes", response.localidade());
        server.verify();
    }

    @Test
    void shouldRejectAMalformedCepWithoutCallingViaCep() {
        ApiException exception = assertThrows(ApiException.class, () -> service.findAddress("123"));
        assertEquals(400, exception.getStatus().value());
        server.verify();
    }

    @Test
    void shouldReportANonexistentCep() {
        server.expect(once(), requestTo("https://viacep.com.br/ws/00000000/json/"))
                .andRespond(withSuccess("{\"erro\":true}", MediaType.APPLICATION_JSON));

        ApiException exception = assertThrows(ApiException.class, () -> service.findAddress("00000000"));
        assertEquals(404, exception.getStatus().value());
        server.verify();
    }

    @Test
    void shouldHandleViaCepFailure() {
        server.expect(once(), requestTo("https://viacep.com.br/ws/08773380/json/"))
                .andRespond(withServerError());

        ApiException exception = assertThrows(ApiException.class, () -> service.findAddress("08773380"));
        assertEquals(503, exception.getStatus().value());
        server.verify();
    }

    @Test
    void shouldCompareTheCityWithTheDistributionCenter() {
        Product product = new Product();
        product.setDistributionCenter("Mogi das Cruzes");
        when(productService.findActiveById("p1")).thenReturn(product);

        server.expect(once(), requestTo("https://viacep.com.br/ws/08773380/json/"))
                .andRespond(withSuccess(
                        "{\"localidade\":\"Mogi das Cruzes\"}",
                        MediaType.APPLICATION_JSON
                ));

        assertTrue(service.isAvailable("p1", "08773380"));
        server.verify();
    }

    @Test
    void shouldReturnFalseForADifferentDistributionCenter() {
        Product product = new Product();
        product.setDistributionCenter("Recife");
        when(productService.findActiveById("p11")).thenReturn(product);

        server.expect(once(), requestTo("https://viacep.com.br/ws/08773380/json/"))
                .andRespond(withSuccess(
                        "{\"localidade\":\"Mogi das Cruzes\"}",
                        MediaType.APPLICATION_JSON
                ));

        assertFalse(service.isAvailable("p11", "08773380"));
        server.verify();
    }
}

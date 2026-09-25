package com.example.crud.service;

import com.example.crud.domain.product.Product;
import com.example.crud.domain.product.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class ProductAvailabilityServiceTest {
    private ProductRepository repository;
    private MockRestServiceServer server;
    private ProductAvailabilityService service;

    @BeforeEach
    void setUp() {
        repository = mock(ProductRepository.class);
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        service = new ProductAvailabilityService(repository, restTemplate);
        Product product = new Product();
        product.setActive(true);
        product.setDistribution_center("Mogi das Cruzes");
        when(repository.findById("p1")).thenReturn(Optional.of(product));
    }

    @Test
    void comparesCityFromViaCep() {
        server.expect(requestTo("https://viacep.com.br/ws/08773380/json/"))
                .andRespond(withSuccess("{\"localidade\":\"Mogi das Cruzes\"}", MediaType.APPLICATION_JSON));
        assertTrue(service.isAvailable("p1", "08773-380"));
        server.verify();
    }

    @Test
    void returnsFalseForDifferentCity() {
        server.expect(requestTo("https://viacep.com.br/ws/50000000/json/"))
                .andRespond(withSuccess("{\"localidade\":\"Recife\"}", MediaType.APPLICATION_JSON));
        assertFalse(service.isAvailable("p1", "50000000"));
        server.verify();
    }

    @Test
    void rejectsMalformedAndUnknownCep() {
        assertEquals(HttpStatus.BAD_REQUEST,
                assertThrows(ResponseStatusException.class, () -> service.isAvailable("p1", "123"))
                        .getStatusCode());
        server.expect(requestTo("https://viacep.com.br/ws/00000000/json/"))
                .andRespond(withSuccess("{\"erro\":true}", MediaType.APPLICATION_JSON));
        assertEquals(HttpStatus.NOT_FOUND,
                assertThrows(ResponseStatusException.class, () -> service.isAvailable("p1", "00000000"))
                        .getStatusCode());
        server.verify();
    }

    @Test
    void handlesExternalFailureAndMissingProduct() {
        server.expect(requestTo("https://viacep.com.br/ws/08773380/json/"))
                .andRespond(withServerError());
        assertEquals(HttpStatus.BAD_GATEWAY,
                assertThrows(ResponseStatusException.class, () -> service.isAvailable("p1", "08773380"))
                        .getStatusCode());
        assertThrows(EntityNotFoundException.class, () -> service.isAvailable("missing", "08773380"));
        server.verify();
    }
}

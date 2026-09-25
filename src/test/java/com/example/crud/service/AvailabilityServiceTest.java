package com.example.crud.service;

import com.example.crud.domain.availability.AvailabilityResponse;
import com.example.crud.domain.product.Product;
import com.example.crud.domain.product.ProductRepository;
import com.example.crud.service.viacep.ViaCepResponse;
import com.example.crud.service.viacep.ViaCepService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AvailabilityServiceTest {

    private ProductRepository repository;
    private ViaCepService viaCepService;
    private AvailabilityService service;

    @BeforeEach
    void setUp() {
        repository = mock(ProductRepository.class);
        viaCepService = mock(ViaCepService.class);
        service = new AvailabilityService(repository, viaCepService);
    }

    private Product product(String distributionCenter) {
        return new Product("p1", "Football Jersey", 5000, true, "clothes", distributionCenter);
    }

    private ViaCepResponse address(String city) {
        return new ViaCepResponse("08773-380", null, null, city, "SP", null);
    }

    @Test
    void shouldReturnTrueWhenCityMatchesDistributionCenter() {
        when(repository.findById("p1")).thenReturn(Optional.of(product("Mogi das Cruzes")));
        when(viaCepService.findAddressByCep("08773380")).thenReturn(address("Mogi das Cruzes"));

        AvailabilityResponse response = service.checkAvailability("p1", "08773380");

        assertTrue(response.available());
    }

    @Test
    void shouldReturnFalseWhenCityIsDifferent() {
        when(repository.findById("p1")).thenReturn(Optional.of(product("Recife")));
        when(viaCepService.findAddressByCep("08773380")).thenReturn(address("Mogi das Cruzes"));

        assertFalse(service.checkAvailability("p1", "08773380").available());
    }

    @Test
    void shouldIgnoreCaseAndAccents() {
        assertTrue(service.isSameCity("São Paulo", "sao paulo"));
        assertTrue(service.isSameCity(" Mogi das  Cruzes ", "MOGI DAS CRUZES"));
        assertFalse(service.isSameCity("Recife", null));
    }

    @Test
    void shouldNotCallViaCepWhenProductDoesNotExist() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.checkAvailability("x", "08773380"));
        verify(viaCepService, never()).findAddressByCep(anyString());
    }
}

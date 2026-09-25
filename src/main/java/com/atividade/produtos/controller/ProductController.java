package com.atividade.produtos.controller;

import com.atividade.produtos.dto.AvailabilityResponse;
import com.atividade.produtos.dto.ProductRequest;
import com.atividade.produtos.dto.ProductResponse;
import com.atividade.produtos.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestHeader("X-User") String user,
                                                  @Valid @RequestBody ProductRequest request) {
        ProductResponse created = service.create(request, user);
        return ResponseEntity.created(URI.create("/products/" + created.id())).body(created);
    }

    @GetMapping
    public List<ProductResponse> list(@RequestParam(value = "category", required = false) String category) {
        return service.list(category);
    }

    @GetMapping("/top5")
    public List<ProductResponse> top5ByPrice() {
        return service.top5ByPrice();
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable("id") String id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable("id") String id,
                                  @Valid @RequestBody ProductRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/inactivate")
    public ResponseEntity<Void> inactivate(@PathVariable("id") String id) {
        service.inactivate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/availability")
    public AvailabilityResponse checkAvailability(@PathVariable("id") String id,
                                                  @RequestParam("cep") String cep) {
        return service.checkAvailability(id, cep);
    }
}
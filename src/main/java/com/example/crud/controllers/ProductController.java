package com.example.crud.controllers;

import com.example.crud.domain.category.RequestCategory;
import com.example.crud.domain.product.Product;
import com.example.crud.domain.product.RequestProduct;
import com.example.crud.service.AddressSearch;
import com.example.crud.service.ProductService;
import com.example.crud.service.ViaCepService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final ViaCepService viaCepService;
    private final AddressSearch addressSearch;

    public ProductController(
            ProductService productService,
            ViaCepService viaCepService,
            AddressSearch addressSearch
    ) {
        this.productService = productService;
        this.viaCepService = viaCepService;
        this.addressSearch = addressSearch;
    }

    @GetMapping
    public ResponseEntity<List<Product>> listProducts() {
        return ResponseEntity.ok(productService.findAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findProduct(@PathVariable String id) {
        return ResponseEntity.ok(productService.findActiveById(id));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> findByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.findByCategory(category));
    }

    @GetMapping("/top5-by-price")
    public ResponseEntity<List<Product>> findTopFiveByPrice() {
        return ResponseEntity.ok(productService.findTopFiveByPrice());
    }

    @GetMapping("/availability/{id}")
    public ResponseEntity<Boolean> verifyAvailability(
            @PathVariable String id,
            @RequestParam String cep
    ) {
        return ResponseEntity.ok(viaCepService.isAvailable(id, cep));
    }

    @GetMapping("/cep")
    public ResponseEntity<String> findCep(
            @RequestParam String state,
            @RequestParam String city,
            @RequestParam String street
    ) {
        return ResponseEntity.ok(addressSearch.searchAddress(state, city, street));
    }

    @PostMapping("/category/{categoryPath}/filter")
    public ResponseEntity<List<Product>> filterWithAllRequestComponents(
            @PathVariable String categoryPath,
            @RequestParam String categoryParam,
            @RequestHeader("X-Category") String categoryHeader,
            @RequestBody @Valid RequestCategory categoryBody
    ) {
        return ResponseEntity.ok(productService.findByMatchingCategories(
                categoryPath,
                categoryParam,
                categoryHeader,
                categoryBody
        ));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody @Valid RequestProduct request) {
        Product product = productService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(product.getId())
                .toUri();
        return ResponseEntity.created(location).body(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String id,
            @RequestBody @Valid RequestProduct request
    ) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateProduct(@PathVariable String id) {
        productService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}

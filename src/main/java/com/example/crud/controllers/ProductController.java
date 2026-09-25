package com.example.crud.controllers;

import com.example.crud.domain.product.Product;
import com.example.crud.domain.product.ProductNotFoundException;
import com.example.crud.domain.product.ProductRepository;
import com.example.crud.domain.product.RequestProduct;
import com.example.crud.services.ViaCepService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ViaCepService viaCepService;

    @PostMapping
    public ResponseEntity<Void> registerProduct(@RequestBody @Valid RequestProduct data) {
        Product newProduct = new Product(data);
        repository.save(newProduct);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestHeader(value = "X-Client-Name", defaultValue = "Postman") String clientName) {
        List<Product> allProducts = repository.findAllByActiveTrue();
        return ResponseEntity.ok()
                .header("X-Client-Name", clientName)
                .body(allProducts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Product> updateProduct(
            @PathVariable String id,
            @RequestBody @Valid RequestProduct data) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setName(data.name());
        product.setPrice(data.price());
        product.setCategory(data.category());
        product.setDistributionCenter(data.distributionCenter());
        repository.save(product);

        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setActive(false);
        repository.save(product);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category")
    public ResponseEntity<List<Product>> getProductsByCategory(@RequestParam String category) {
        List<Product> products = repository.findAllByActiveTrueAndCategory(category);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/top-five")
    public ResponseEntity<List<Product>> getTopFiveProductsByPrice() {
        List<Product> products = repository.findTop5ByActiveTrueOrderByPriceDesc();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable String id,
            @RequestParam String cep) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        boolean available = viaCepService.checkAvailability(cep, product.getDistributionCenter());

        return ResponseEntity.ok(available);
    }
}

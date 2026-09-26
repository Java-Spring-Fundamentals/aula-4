package com.example.crud.controllers;

import com.example.crud.domain.category.RequestCategory;
import com.example.crud.domain.product.Product;
import com.example.crud.domain.product.ProductRepository;
import com.example.crud.domain.product.RequestProduct;
import com.example.crud.service.AddressSearch;
import com.example.crud.service.ViaCepService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductRepository repository;
    private final AddressSearch addressSearch;
    private final ViaCepService viaCepService;

    public ProductController(
            ProductRepository repository,
            AddressSearch addressSearch,
            ViaCepService viaCepService) {
        this.repository = repository;
        this.addressSearch = addressSearch;
        this.viaCepService = viaCepService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(repository.findAllByActiveTrue());
    }

    @GetMapping("/availability/{id}")
    public ResponseEntity<Boolean> verificarDisponibilidade(
            @PathVariable("id") String id,
            @RequestParam("cep") String cep) {

        boolean disponivel = viaCepService.verificarDisponibilidade(id, cep);

        return ResponseEntity.ok(disponivel);
    }

    @GetMapping("/cep")
    public ResponseEntity<String> buscarCep(
            @RequestParam("state") String state,
            @RequestParam("city") String city,
            @RequestParam("street") String street) {

        String cep = addressSearch.searchAddress(state, city, street);

        return ResponseEntity.ok(cep);
    }

    @GetMapping("/endpoint1")
    public ResponseEntity<List<Product>> getProductsByCategory(
            @RequestParam("categoryAsParam") String category) {

        List<Product> products =
                repository.findAllByCategoryAndActiveTrue(category);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/endpoint2/{id}")
    public ResponseEntity<Product> getProduct(
            @PathVariable("id") String id) {

        return ResponseEntity.ok(buscarProduto(id));
    }

    @GetMapping("/endpoint3/top5byprice")
    public ResponseEntity<List<Product>> getTopFiveProducts() {
        List<Product> products = repository.findAllByActiveTrue()
                .stream()
                .sorted(Comparator.comparingInt(Product::getPrice).reversed())
                .limit(5)
                .toList();

        return ResponseEntity.ok(products);
    }

    // Exemplo da aula com as quatro formas de receber dados.
    @GetMapping("/category/{categoryAsPath}")
    public ResponseEntity<List<Product>> getProductsByCategory(
            @RequestHeader("categoryAsHeader") String categoryAsHeader,
            @PathVariable("categoryAsPath") String categoryAsPath,
            @RequestBody @Valid RequestCategory categoryAsBody,
            @RequestParam("categoryAsParam") String categoryAsParam) {

        if (!categoryAsPath.equals(categoryAsParam)
                || !categoryAsPath.equals(categoryAsHeader)
                || !categoryAsPath.equals(categoryAsBody.category())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe a mesma categoria no path, parametro, cabecalho e corpo.");
        }

        List<Product> products =
                repository.findAllByCategoryAndActiveTrue(categoryAsParam);

        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<Product> registerProduct(
            @RequestBody @Valid RequestProduct data) {

        Product product = repository.save(new Product(data));

        URI location = URI.create("/product/endpoint2/" + product.getId());

        return ResponseEntity.created(location).body(product);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<Product> updateProduct(
            @RequestBody @Valid RequestProduct data) {

        if (data.id() == null || data.id().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o id do produto para atualizar.");
        }

        Product product = buscarProduto(data.id());

        product.setName(data.name());
        product.setPrice(data.price());
        product.setCategory(data.category());
        product.setDistributionCenter(data.distributionCenter());

        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteProduct(
            @PathVariable("id") String id) {

        Product product = buscarProduto(id);
        product.setActive(false);

        return ResponseEntity.noContent().build();
    }

    private Product buscarProduto(String id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Produto nao encontrado."));
    }
}
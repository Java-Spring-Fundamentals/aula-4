package com.example.crud.controllers;

import com.example.crud.domain.category.RequestCategory;
import com.example.crud.domain.product.DistributionCenter;
import com.example.crud.domain.product.DistributionCenterCount;
import com.example.crud.domain.product.Product;
import com.example.crud.domain.product.ProductRepository;
import com.example.crud.domain.product.RequestProduct;
import com.example.crud.domain.product.TopExpensiveProducts;
import com.example.crud.service.AddressSearch;
import com.example.crud.service.AvailabilityService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductRepository repository;
    private final AddressSearch addressSearch;
    private final AvailabilityService availabilityService;

    @Autowired
    public ProductController(ProductRepository repository, AddressSearch addressSearch, AvailabilityService availabilityService) {
        this.repository = repository;
        this.addressSearch = addressSearch;
        this.availabilityService = availabilityService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(){
        var allProducts = repository.findAllByActiveTrue();
        return ResponseEntity.ok(allProducts);
    }


    @GetMapping("/availability/{id}") // true if the cep city matches the product distribution center
    public ResponseEntity<Boolean> checkAvailability(@PathVariable String id, @RequestParam String cep){
        boolean available = availabilityService.isAvailable(id, cep);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/cep")
    public ResponseEntity<String> verifyAvailability(@RequestParam String state, @RequestParam String city, @RequestParam String street){
        String cep = addressSearch.searchAddress(state, city, street);
        return ResponseEntity.ok(cep);
    }


    @GetMapping(params = "category") // derived query
    public ResponseEntity<List<Product>> getActiveProductsByCategory(@RequestParam String category){
        var products = repository.findAllByCategoryAndActiveTrue(category);
        return ResponseEntity.ok(products);
    }

    @GetMapping(params = "minPrice") // derived query with GreaterThan
    public ResponseEntity<List<Product>> getProductsByMinPrice(@RequestParam Integer minPrice){
        var products = repository.findAllByActiveTrueAndPriceGreaterThan(minPrice);
        return ResponseEntity.ok(products);
    }

    @GetMapping(params = "nameContains") // jpql
    public ResponseEntity<List<Product>> getProductsByName(@RequestParam String nameContains){
        var products = repository.searchActiveByName(nameContains);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/top-expensive") // top 3 calculated in the model class
    public ResponseEntity<List<Product>> getTopExpensiveProducts(){
        var topProducts = new TopExpensiveProducts(repository.findAllByActiveTrue()).top(3);
        return ResponseEntity.ok(topProducts);
    }

    @GetMapping(value = "/distribution-center/{cityOrState}", params = "category") // derived query with In
    public ResponseEntity<List<Product>> getProductsByDistributionCenterAndCategory(
            @PathVariable String cityOrState,
            @RequestParam String category
    ){
        List<DistributionCenter> centers = DistributionCenter.fromCityOrState(cityOrState);
        if (centers.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        var products = repository.findAllByActiveTrueAndDistributionCenterInAndCategory(centers, category);
        return ResponseEntity.ok(products);
    }


    @GetMapping("/distribution-center/{cityOrState}") // Recife or PE, Mogi das Cruzes or SP...
    public ResponseEntity<List<Product>> getProductsByDistributionCenter(@PathVariable String cityOrState){
        List<DistributionCenter> centers = DistributionCenter.fromCityOrState(cityOrState);
        if (centers.isEmpty()) {
            return ResponseEntity.ok(List.of()); // valid state without a distribution center (RJ, MG...)
        }
        var products = repository.findAllByActiveTrueAndDistributionCenterIn(centers);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/distribution-center/count")
    public ResponseEntity<Map<String, Long>> countProductsByDistributionCenter(){
        List<DistributionCenterCount> counts = repository.countActiveByDistributionCenter();
        Map<String, Long> response = new LinkedHashMap<>();
        for (DistributionCenterCount count : counts) {
            response.put(count.getDistributionCenter().getCity(), count.getTotal());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/distribution-center/count/state")
    public ResponseEntity<Map<String, Long>> countProductsByState(){
        List<DistributionCenterCount> counts = repository.countActiveByDistributionCenter();
        Map<String, Long> response = new LinkedHashMap<>();
        for (DistributionCenterCount count : counts) {
            response.merge(count.getDistributionCenter().getUf(), count.getTotal(), Long::sum);
        }
        return ResponseEntity.ok(response);
    }


    @GetMapping("/endpoint1") //products from only one category
    public ResponseEntity<List<Product>> getAllProducts1(@RequestParam String categoryAsParam){
        var allProducts = repository.findAllByCategory(categoryAsParam);
        return ResponseEntity.ok(allProducts);
    }

    @GetMapping("/endpoint2/{id}") //only one product
    public ResponseEntity<Product> getProduct(@PathVariable String id){
        Product product = repository.findByIdAndActiveTrue(id)
                .orElseThrow(EntityNotFoundException::new);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/endpoint3/top5byprice") // top 5 product by price
    public ResponseEntity<List<Product>> getAllProducts3(){
        var allProducts = repository.findAllByActiveTrue();

        List<Product> topFive = allProducts
                .stream()
                .sorted(Comparator.comparingInt(Product::getPrice).reversed())
                .limit(5)
                .collect(Collectors.toList());

        return ResponseEntity.ok(topFive);
    }

    @GetMapping("/category/{categoryAsPath}") //all REST Components
    public ResponseEntity<List<Product>> getProductsByCategory(
            @RequestHeader String categoryAsHeader,
            @PathVariable String categoryAsPath,
            @RequestBody @Valid RequestCategory categoryAsBody,
            @RequestParam String categoryAsParam
    ){
        var allProducts = repository.findAllByActiveTrue();
        List<Product> filteredProducts = new ArrayList<>();

        for (int i = 0; i < allProducts.size(); i++) {
            Product product = allProducts.get(i);
            if (categoryAsParam.equals(product.getCategory())) {
                filteredProducts.add(product);
            }
        }
        return ResponseEntity.ok(filteredProducts);
    }

    @PostMapping
    public ResponseEntity<Void> registerProduct(@RequestBody @Valid RequestProduct data){
        Product newProduct = new Product(data);
        repository.save(newProduct);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @Transactional
    public ResponseEntity<Product> updateProduct(@RequestBody @Valid RequestProduct data){
        if (data.id() == null) {
            throw new EntityNotFoundException();
        }
        Optional<Product> optionalProduct = repository.findById(data.id());
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setName(data.name());
            product.setPrice(data.price());
            product.setDistributionCenter(data.distributionCenter());
            return ResponseEntity.ok(product);
        } else {
            throw new EntityNotFoundException();
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteProduct(@PathVariable String id){
        Optional<Product> optionalProduct = repository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setActive(false);
            return ResponseEntity.noContent().build();
        } else {
            throw new EntityNotFoundException();
        }
    }

}
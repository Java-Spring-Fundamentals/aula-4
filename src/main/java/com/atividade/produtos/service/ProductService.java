package com.atividade.produtos.service;

import com.atividade.produtos.dto.AvailabilityResponse;
import com.atividade.produtos.dto.ProductRequest;
import com.atividade.produtos.dto.ProductResponse;
import com.atividade.produtos.exception.ProductNotFoundException;
import com.atividade.produtos.model.Product;
import com.atividade.produtos.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final ViaCepService viaCepService;

    public ProductService(ProductRepository repository, ViaCepService viaCepService) {
        this.repository = repository;
        this.viaCepService = viaCepService;
    }

    @Transactional
    public ProductResponse create(ProductRequest request, String user) {
        Product product = new Product();
        copyFields(request, product);
        product.setCreatedBy(user);
        return ProductResponse.from(repository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> list(String category) {
        List<Product> products;
        if (category == null || category.isBlank()) {
            products = repository.findByActiveTrueOrderByNameAsc();
        } else {
            products = repository.findByCategoryIgnoreCaseAndActiveTrueOrderByNameAsc(category.trim());
        }
        return products.stream().map(ProductResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(String id) {
        return ProductResponse.from(getProductOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> top5ByPrice() {
        return repository.findTop5ByActiveTrueOrderByPriceDesc().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional
    public ProductResponse update(String id, ProductRequest request) {
        Product product = getProductOrThrow(id);
        copyFields(request, product);
        return ProductResponse.from(repository.saveAndFlush(product));
    }

    @Transactional
    public void inactivate(String id) {
        Product product = getProductOrThrow(id);
        product.setActive(false);
    }

    public AvailabilityResponse checkAvailability(String productId, String cep) {
        Product product = getProductOrThrow(productId);
        String city = viaCepService.findCityByCep(cep);
        boolean available = isSameCity(city, product.getDistributionCenter());
        return new AvailabilityResponse(product.getId(), cep, city, product.getDistributionCenter(), available);
    }

    boolean isSameCity(String city, String distributionCenter) {
        if (city == null || distributionCenter == null) {
            return false;
        }
        return normalize(city).equals(normalize(distributionCenter));
    }

    private String normalize(String text) {
        String withoutAccents = Normalizer.normalize(text.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return withoutAccents.toLowerCase(Locale.ROOT);
    }

    private Product getProductOrThrow(String id) {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    private void copyFields(ProductRequest request, Product product) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setCategory(request.category());
        product.setPrice(request.price());
        product.setDistributionCenter(request.distributionCenter());
    }
}
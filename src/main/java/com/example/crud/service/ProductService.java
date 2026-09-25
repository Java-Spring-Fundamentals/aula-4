package com.example.crud.service;

import com.example.crud.domain.category.RequestCategory;
import com.example.crud.domain.product.Product;
import com.example.crud.domain.product.ProductRepository;
import com.example.crud.domain.product.RequestProduct;
import com.example.crud.infra.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> findAllActive() {
        return repository.findAllByActiveTrue();
    }

    public Product findActiveById(String id) {
        return repository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Produto não encontrado: " + id));
    }

    public List<Product> findByCategory(String category) {
        return repository.findAllByCategoryIgnoreCaseAndActiveTrue(category);
    }

    public List<Product> findTopFiveByPrice() {
        return repository.findTop5ByActiveTrueOrderByPriceDesc();
    }

    public List<Product> findByMatchingCategories(
            String categoryPath,
            String categoryParam,
            String categoryHeader,
            RequestCategory categoryBody
    ) {
        boolean categoriesMatch = Stream.of(categoryParam, categoryHeader, categoryBody.category())
                .allMatch(category -> categoryPath.equalsIgnoreCase(category.trim()));

        if (!categoriesMatch) {
            throw new IllegalArgumentException("As categorias informadas devem ser iguais");
        }
        return findByCategory(categoryPath);
    }

    @Transactional
    public Product create(RequestProduct request) {
        return repository.save(new Product(request));
    }

    @Transactional
    public Product update(String id, RequestProduct request) {
        Product product = findActiveById(id);
        product.update(request);
        return product;
    }

    @Transactional
    public void deactivate(String id) {
        Product product = findActiveById(id);
        product.deactivate();
    }
}

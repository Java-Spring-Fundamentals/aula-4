package com.example.crud.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findAllByActiveTrue();

    List<Product> findAllByCategoryIgnoreCaseAndActiveTrue(String category);

    Optional<Product> findByIdAndActiveTrue(String id);

    List<Product> findTop5ByActiveTrueOrderByPriceDesc();
}

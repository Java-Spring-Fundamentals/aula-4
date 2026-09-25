package com.exemplo.produtoapi.repository;

import com.exemplo.produtoapi.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryIgnoreCaseAndActiveTrue(String category);

    List<Product> findByActiveTrueOrderByPriceDesc(Pageable pageable);
}

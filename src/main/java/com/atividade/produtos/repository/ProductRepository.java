package com.atividade.produtos.repository;

import com.atividade.produtos.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByActiveTrueOrderByNameAsc();

    List<Product> findByCategoryIgnoreCaseAndActiveTrueOrderByNameAsc(String category);

    List<Product> findTop5ByActiveTrueOrderByPriceDesc();
}
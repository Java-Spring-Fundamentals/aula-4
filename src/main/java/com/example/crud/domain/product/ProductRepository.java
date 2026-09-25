package com.example.crud.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findAllByActiveTrue();

    @Query("SELECT p FROM product p WHERE p.category = :category AND p.active = true")
    List<Product> findAllByCategory(@Param("category") String category);

    List<Product> findAllByActiveTrueAndCategory(String category);

    List<Product> findAllByActiveTrueAndDistributionCenter(DistributionCenter distributionCenter);

    List<Product> findAllByActiveTrueAndDistributionCenterAndCategory(DistributionCenter distributionCenter, String category);

    List<Product> findAllByActiveTrueAndPriceGreaterThan(Integer price);

    @Query("SELECT p FROM product p WHERE p.active = true AND LOWER(p.name) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Product> findAllActiveByNameContaining(@Param("term") String term);

    @Query("SELECT p.distributionCenter, COUNT(p) FROM product p WHERE p.active = true GROUP BY p.distributionCenter")
    List<Object[]> countActiveByDistributionCenter();
}

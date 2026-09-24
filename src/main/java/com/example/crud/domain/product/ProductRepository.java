package com.example.crud.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findAllByActiveTrue();

    @Query("SELECT p FROM product p WHERE p.category = :category AND p.active = true")
    List<Product> findAllByCategory(@Param("category") String category);

    List<Product> findAllByCategoryAndActiveTrue(String category);

    Optional<Product> findByIdAndActiveTrue(String id);

    List<Product> findAllByActiveTrueAndPriceGreaterThan(Integer price);

    List<Product> findAllByActiveTrueAndDistributionCenterIn(List<DistributionCenter> distributionCenters);

    List<Product> findAllByActiveTrueAndDistributionCenterInAndCategory(List<DistributionCenter> distributionCenters, String category);

    @Query("SELECT p FROM product p WHERE p.active = true AND LOWER(p.name) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Product> searchActiveByName(@Param("term") String term);

    @Query("SELECT p.distributionCenter as distributionCenter, COUNT(p) as total " +
            "FROM product p WHERE p.active = true GROUP BY p.distributionCenter")
    List<DistributionCenterCount> countActiveByDistributionCenter();

}
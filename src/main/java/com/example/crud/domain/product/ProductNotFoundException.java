package com.example.crud.domain.product;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String id) {
        super("Product not found with ID: " + id);
    }
}

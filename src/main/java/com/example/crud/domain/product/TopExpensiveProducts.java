package com.example.crud.domain.product;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TopExpensiveProducts {

    private final List<Product> products;

    public TopExpensiveProducts(List<Product> products) {
        this.products = products;
    }

    public List<Product> top(int limit) {
        List<Product> sorted = new ArrayList<>(products);
        sorted.sort(Comparator.comparing(Product::getPrice).reversed());
        return new ArrayList<>(sorted.subList(0, Math.min(limit, sorted.size())));
    }
}
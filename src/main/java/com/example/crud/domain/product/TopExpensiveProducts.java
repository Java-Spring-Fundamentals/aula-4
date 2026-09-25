package com.example.crud.domain.product;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TopExpensiveProducts {
    private static final int LIMIT = 3;

    private final List<Product> products;

    public TopExpensiveProducts(List<Product> products) {
        this.products = products;
    }

    public List<Product> getTop() {
        List<Product> sorted = new ArrayList<>(products);
        sorted.sort(Comparator.comparing(Product::getPrice).reversed());
        return sorted.subList(0, Math.min(LIMIT, sorted.size()));
    }
}

package com.example.leafdex;

import java.lang.reflect.Constructor;

public class Product {
    private String product;

    public Product(String product) {
        this.product = product;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
}

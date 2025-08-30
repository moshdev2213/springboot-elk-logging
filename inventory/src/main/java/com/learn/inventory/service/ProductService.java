package com.learn.inventory.service;

import java.util.List;
import java.util.Optional;

import com.learn.inventory.entity.Product;

public interface ProductService {
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
    Product createProduct(Product product);
    Product updateProduct(Long id, Product productDetails);
    void deleteProduct(Long id);
    void updateStockQuantity(Long productId, Integer quantity);
}
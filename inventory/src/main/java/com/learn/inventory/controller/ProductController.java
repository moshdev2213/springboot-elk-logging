package com.learn.inventory.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.learn.inventory.entity.Product;
import com.learn.inventory.service.ProductService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        log.info("GET /api/products - Fetching all products");
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        log.info("GET /api/products/{} - Fetching product by id", id);
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        log.info("POST /api/products - Creating new product: {}", product.getName());
        try {
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (RuntimeException e) {
            log.error("Failed to create product: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        log.info("PUT /api/products/{} - Updating product", id);
        try {
            Product updatedProduct = productService.updateProduct(id, productDetails);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            log.error("Failed to update product: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("DELETE /api/products/{} - Deleting product", id);
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Failed to delete product: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/stock")
    public ResponseEntity<Void> updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        log.info("POST /api/products/{}/stock - Updating stock by quantity: {}", id, quantity);
        try {
            productService.updateStockQuantity(id, quantity);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Failed to update stock: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}

package com.learn.inventory.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.learn.inventory.entity.Product;
import com.learn.inventory.repository.ProductRepository;
import com.learn.inventory.service.ProductService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;

    @Override
    public List<Product> getAllProducts() {
        log.info("Fetching All Products");
        List<Product> products = productRepository.findAll();
        log.info("Found {} products", products.size());
        return products;
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            log.info("Product found: {}", product.get().getName());
        } else {
            log.warn("Product not found with id: {}", id);
        }
        return product;
    }

    @Override
    public Product createProduct(Product product) {
        log.info("Creating new product: {}", product.getName());
        
        // Check if product with same name already exists
        Optional<Product> existingProduct = productRepository.findByName(product.getName());
        if (existingProduct.isPresent()) {
            log.error("Product with name '{}' already exists", product.getName());
            throw new RuntimeException("Product with name '" + product.getName() + "' already exists");
        }
        
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with id: {}", savedProduct.getId());
        return savedProduct;
    }

    @Override
    public Product updateProduct(Long id, Product productDetails) {
log.info("Updating product with id: {}", id);
        
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isEmpty()) {
            log.error("Product not found with id: {}", id);
            throw new RuntimeException("Product not found with id: " + id);
        }
        
        Product product = existingProduct.get();
        
        // Check if name is being changed and if new name already exists
        if (!product.getName().equals(productDetails.getName())) {
            Optional<Product> productWithSameName = productRepository.findByNameAndIdNot(productDetails.getName(), id);
            if (productWithSameName.isPresent()) {
                log.error("Product with name '{}' already exists", productDetails.getName());
                throw new RuntimeException("Product with name '" + productDetails.getName() + "' already exists");
            }
        }
        
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStockQuantity(productDetails.getStockQuantity());
        product.setCategory(productDetails.getCategory());
        product.setUpdatedAt(LocalDateTime.now());
        
        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully: {}", updatedProduct.getName());
        return updatedProduct;
    }

    @Override
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        
        if (!productRepository.existsById(id)) {
            log.error("Product not found with id: {}", id);
            throw new RuntimeException("Product not found with id: " + id);
        }
        
        productRepository.deleteById(id);
        log.info("Product deleted successfully with id: {}", id);
    }

    @Override
    public void updateStockQuantity(Long productId, Integer quantity) {
        log.info("Updating stock quantity for product id: {} by quantity: {}", productId, quantity);
        
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            log.error("Product not found with id: {}", productId);
            throw new RuntimeException("Product not found with id: " + productId);
        }
        
        Product product = productOpt.get();
        int newStock = product.getStockQuantity() - quantity;
        
        if (newStock < 0) {
            log.error("Insufficient stock for product: {}. Available: {}, Requested: {}", 
                     product.getName(), product.getStockQuantity(), quantity);
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }
        
        product.setStockQuantity(newStock);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
        
        log.info("Stock updated for product: {}. New stock: {}", product.getName(), newStock);
    }
}

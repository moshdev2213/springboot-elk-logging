package com.learn.inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.inventory.entity.Product;

public interface ProductRepository extends JpaRepository<Product,Long> {
    Optional<Product> findByName(String name);
    Optional<Product> findByNameAndIdNot(String name,Long id);
}
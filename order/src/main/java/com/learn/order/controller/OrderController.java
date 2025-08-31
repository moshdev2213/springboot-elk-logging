package com.learn.order.controller;

import com.learn.order.dto.CreateOrderRequest;
import com.learn.order.entity.Order;
import com.learn.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    
    private final OrderService orderService;
    
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        log.info("GET /api/orders - Fetching all orders");
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        log.info("GET /api/orders/{} - Fetching order by id", id);
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/customer/{email}")
    public ResponseEntity<List<Order>> getOrdersByCustomerEmail(@PathVariable String email) {
        log.info("GET /api/orders/customer/{} - Fetching orders by customer email", email);
        List<Order> orders = orderService.getOrdersByCustomerEmail(email);
        return ResponseEntity.ok(orders);
    }
    
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("POST /api/orders - Creating new order for customer: {}", request.getCustomerName());
        try {
            Order createdOrder = orderService.createOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (Exception e) {
            log.error("Failed to create order: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        log.info("PUT /api/orders/{}/status - Updating order status to: {}", id, status);
        try {
            Order updatedOrder = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            log.error("Failed to update order status: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.info("DELETE /api/orders/{} - Deleting order", id);
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Failed to delete order: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}

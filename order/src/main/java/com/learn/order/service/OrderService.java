package com.learn.order.service;

import java.util.List;
import java.util.Optional;

import com.learn.order.dto.CreateOrderRequest;
import com.learn.order.entity.Order;

public interface OrderService {
    List<Order> getAllOrders();

    Optional<Order> getOrderById(Long id);

    List<Order> getOrdersByCustomerEmail(String customerEmail);

    Order createOrder(CreateOrderRequest request);

    Order updateOrderStatus(Long id, String status);

    void deleteOrder(Long id);
}

package com.learn.order.service.impl;

import com.learn.common.dto.OrderCreatedEvent;
import com.learn.order.dto.CreateOrderRequest;
import com.learn.order.entity.Order;
import com.learn.order.entity.OrderItem;
import com.learn.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.learn.order.service.OrderService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @Value("${kafka.topic.order-created}")
    private String orderCreatedTopic;

    @Override
    public List<Order> getAllOrders() {
        log.info("Fetching all orders");
        List<Order> orders = orderRepository.findAll();
        log.info("Found {} orders", orders.size());
        return orders;
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        log.info("Fetching order with id: {}", id);
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            log.info("Order found with id: {}, customer: {}", id, order.get().getCustomerName());
        } else {
            log.warn("Order not found with id: {}", id);
        }
        return order;
    }

    @Override
    public List<Order> getOrdersByCustomerEmail(String customerEmail) {
        log.info("Fetching orders for customer email: {}", customerEmail);
        List<Order> orders = orderRepository.findByCustomerEmail(customerEmail);
        log.info("Found {} orders for customer: {}", orders.size(), customerEmail);
        return orders;
    }

    @Override
    public Order createOrder(CreateOrderRequest request) {
        log.info("Creating new order for customer: {}", request.getCustomerName());

        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // Create order items (simplified - in real scenario, you'd fetch product
        // details)
        List<OrderItem> orderItems = request.getOrderItems().stream()
                .map(item -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProductId(item.getProductId());
                    orderItem.setProductName("Product " + item.getProductId()); // Simplified
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setUnitPrice(BigDecimal.valueOf(10.00)); // Simplified price
                    orderItem.setTotalPrice(BigDecimal.valueOf(10.00).multiply(BigDecimal.valueOf(item.getQuantity())));
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);

        // Calculate total amount
        BigDecimal totalAmount = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with id: {}, total amount: {}", savedOrder.getId(), totalAmount);

        // Publish event to Kafka
        publishOrderCreatedEvent(savedOrder);

        return savedOrder;
    }

    @Override
    public Order updateOrderStatus(Long id, String status) {
        log.info("Updating order status for id: {} to: {}", id, status);

        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            log.error("Order not found with id: {}", id);
            throw new RuntimeException("Order not found with id: " + id);
        }

        Order order = orderOpt.get();
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated successfully: {} -> {}", id, status);
        return updatedOrder;
    }

    @Override
    public void deleteOrder(Long id) {
        log.info("Deleting order with id: {}", id);

        if (!orderRepository.existsById(id)) {
            log.error("Order not found with id: {}", id);
            throw new RuntimeException("Order not found with id: " + id);
        }

        orderRepository.deleteById(id);
        log.info("Order deleted successfully with id: {}", id);
    }

    private void publishOrderCreatedEvent(Order order) {
        try {
            OrderCreatedEvent event = new OrderCreatedEvent();
            event.setOrderId(order.getId());
            event.setCustomerName(order.getCustomerName());
            event.setCustomerEmail(order.getCustomerEmail());
            event.setStatus(order.getStatus());
            event.setTotalAmount(order.getTotalAmount());
            event.setCreatedAt(order.getCreatedAt());

            List<OrderCreatedEvent.OrderItemEvent> itemEvents = order.getOrderItems().stream()
                    .map(item -> {
                        OrderCreatedEvent.OrderItemEvent itemEvent = new OrderCreatedEvent.OrderItemEvent();
                        itemEvent.setProductId(item.getProductId());
                        itemEvent.setProductName(item.getProductName());
                        itemEvent.setQuantity(item.getQuantity());
                        itemEvent.setUnitPrice(item.getUnitPrice());
                        itemEvent.setTotalPrice(item.getTotalPrice());
                        return itemEvent;
                    })
                    .collect(Collectors.toList());

            event.setOrderItems(itemEvents);

            kafkaTemplate.send(orderCreatedTopic, event);
            log.info("Order created event published to Kafka for order: {}", order.getId());

        } catch (Exception e) {
            log.error("Failed to publish order created event for order: {}", order.getId(), e);
            // In production, you might want to handle this differently (e.g., retry
            // mechanism)
        }
    }
}

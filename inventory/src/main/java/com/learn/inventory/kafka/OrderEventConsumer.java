package com.learn.inventory.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.learn.common.dto.OrderCreatedEvent;
import com.learn.inventory.service.ProductService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class OrderEventConsumer {
    private final ProductService productService;
    // private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topic.order-created}", groupId = "${kafka.group-id.inventory}")
    public void consumeOrderCreatedEvent(OrderCreatedEvent orderCreatedEvent) {
        try {
            log.info("Received order created event: {}", orderCreatedEvent);
            // OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);
            log.info("Parsed order created event for order: {}, customer: {}",
                    orderCreatedEvent.getOrderId(), orderCreatedEvent.getCustomerName());
            // Process each order item and update stock
            for (OrderCreatedEvent.OrderItemEvent itemEvent : orderCreatedEvent.getOrderItems()) {
                try {
                    log.info("Processing order item: productId={}, quantity={}",
                            itemEvent.getProductId(), itemEvent.getQuantity());

                    productService.updateStockQuantity(itemEvent.getProductId(), itemEvent.getQuantity());

                    log.info("Successfully updated stock for product: {}", itemEvent.getProductId());

                } catch (Exception e) {
                    log.error("Failed to update stock for product: {}, order: {}",
                            itemEvent.getProductId(), orderCreatedEvent.getOrderId(), e);
                    // In production, you might want to implement dead letter queue or retry
                    // mechanism
                }
            }
        } catch (Exception e) {
            log.error("Unexpected error processing order created event: {}", orderCreatedEvent, e);
        }
    }
}

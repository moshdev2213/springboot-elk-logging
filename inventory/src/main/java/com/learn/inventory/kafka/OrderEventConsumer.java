package com.learn.inventory.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.inventory.event.OrderCreatedEvent;
import com.learn.inventory.service.ProductService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class OrderEventConsumer {
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topic.order-created}", groupId = "${kafka.group-id.inventory}")
    public void consumeOrderCreatedEvent(String message) {
        try {
            log.info("Received order created event: {}", message);
            OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);
            log.info("Parsed order created event for order: {}, customer: {}",
                    event.getOrderId(), event.getCustomerName());
            // Process each order item and update stock
            for (OrderCreatedEvent.OrderItemEvent itemEvent : event.getOrderItems()) {
                try {
                    log.info("Processing order item: productId={}, quantity={}",
                            itemEvent.getProductId(), itemEvent.getQuantity());

                    productService.updateStockQuantity(itemEvent.getProductId(), itemEvent.getQuantity());

                    log.info("Successfully updated stock for product: {}", itemEvent.getProductId());

                } catch (Exception e) {
                    log.error("Failed to update stock for product: {}, order: {}",
                            itemEvent.getProductId(), event.getOrderId(), e);
                    // In production, you might want to implement dead letter queue or retry
                    // mechanism
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse order created event: {}", message, e);
        } catch (Exception e) {
            log.error("Unexpected error processing order created event: {}", message, e);
        }
    }
}

package com.learn.inventory.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    
    private Long orderId;
    private String customerName;
    private String customerEmail;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private List<OrderItemEvent> orderItems;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemEvent {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }
}

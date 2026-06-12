package com.generic.e_commerce.order.dto.response;

import com.generic.e_commerce.order.model.Order;
import com.generic.e_commerce.order.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal total;
    private List<OrderItemResponse> items;

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal subtotal;

        public static OrderItemResponse fromEntity(OrderItem item) {
            return OrderItemResponse.builder()
                    .id(item.getId())
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .subtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .build();
        }
    }

    public static OrderResponse fromEntity(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(OrderItemResponse::fromEntity)
                .toList();
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .orderDate(order.getOrderDate())
                .status(order.getStatus().name())
                .total(order.getTotal())
                .items(itemResponses)
                .build();
    }
}

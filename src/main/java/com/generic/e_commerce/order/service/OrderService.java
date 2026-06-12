package com.generic.e_commerce.order.service;

import com.generic.e_commerce.order.dto.request.CreateOrderRequest;
import com.generic.e_commerce.order.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(Long userId, CreateOrderRequest request);
    OrderResponse getOrderById(Long userId, Long orderId);
    List<OrderResponse> getUserOrders(Long userId);
    List<OrderResponse> getUserOrdersByStatus(Long userId, String status);
    void cancelOrder(Long userId, Long orderId);
}

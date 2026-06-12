package com.generic.e_commerce.order.controller;

import com.generic.e_commerce.order.dto.request.CreateOrderRequest;
import com.generic.e_commerce.order.dto.response.OrderResponse;
import com.generic.e_commerce.order.service.OrderService;
import com.generic.e_commerce.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader("X-User-Email") String email,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(1L, request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @RequestHeader("X-User-Email") String email,
            @RequestParam(required = false) String status) {
        if (status != null) {
            return ResponseEntity.ok(orderService.getUserOrdersByStatus(1L, status));
        }
        return ResponseEntity.ok(orderService.getUserOrders(1L));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(1L, orderId));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse> cancelOrder(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long orderId) {
        orderService.cancelOrder(1L, orderId);
        return ResponseEntity.ok(new ApiResponse("Order cancelled successfully"));
    }
}

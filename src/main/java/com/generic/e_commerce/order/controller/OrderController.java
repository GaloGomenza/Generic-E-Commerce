package com.generic.e_commerce.order.controller;

import com.generic.e_commerce.order.dto.request.CreateOrderRequest;
import com.generic.e_commerce.order.dto.response.OrderResponse;
import com.generic.e_commerce.order.service.OrderService;
import com.generic.e_commerce.shared.dto.ApiResponse;
import com.generic.e_commerce.shared.exception.ResourceNotFoundException;
import com.generic.e_commerce.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    private Long getUserId(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con el email: " + email))
                .getId();
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader("X-User-Email") String email,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(getUserId(email), request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @RequestHeader("X-User-Email") String email,
            @RequestParam(required = false) String status) {
        Long userId = getUserId(email);
        if (status != null) {
            return ResponseEntity.ok(orderService.getUserOrdersByStatus(userId, status));
        }
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(getUserId(email), orderId));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse> cancelOrder(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long orderId) {
        orderService.cancelOrder(getUserId(email), orderId);
        return ResponseEntity.ok(new ApiResponse("La orden se canceló exitosamente."));
    }
}

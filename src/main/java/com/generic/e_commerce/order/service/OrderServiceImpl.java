package com.generic.e_commerce.order.service;

import com.generic.e_commerce.cart.model.CartItem;
import com.generic.e_commerce.cart.repository.CartItemRepository;
import com.generic.e_commerce.order.dto.request.CreateOrderRequest;
import com.generic.e_commerce.order.dto.response.OrderResponse;
import com.generic.e_commerce.order.model.Order;
import com.generic.e_commerce.order.model.OrderItem;
import com.generic.e_commerce.order.repository.OrderRepository;
import com.generic.e_commerce.product.repository.ProductRepository;
import com.generic.e_commerce.shared.exception.ResourceNotFoundException;
import com.generic.e_commerce.shared.exception.UnauthorizedException;
import com.generic.e_commerce.user.model.User;
import com.generic.e_commerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<CartItem> cartItems;
        if (request.getCartItemIds() != null && !request.getCartItemIds().isEmpty()) {
            cartItems = cartItemRepository.findAllById(request.getCartItemIds()).stream()
                    .filter(ci -> ci.getUser().getId().equals(userId))
                    .toList();
        } else {
            cartItems = cartItemRepository.findByUserId(userId);
        }

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("No items to order");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getProduct().getPrice())
                    .build();
            orderItems.add(orderItem);
            total = total.add(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            cartItem.getProduct().setStock(cartItem.getProduct().getStock() - cartItem.getQuantity());
            productRepository.save(cartItem.getProduct());
        }

        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(Order.OrderStatus.PENDING)
                .total(total)
                .items(orderItems)
                .build();

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }

        order = orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems);

        return OrderResponse.fromEntity(order);
    }

    @Override
    public OrderResponse getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        if (!order.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Order does not belong to this user");
        }
        return OrderResponse.fromEntity(order);
    }

    @Override
    public List<OrderResponse> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId).stream()
                .map(OrderResponse::fromEntity)
                .toList();
    }

    @Override
    public List<OrderResponse> getUserOrdersByStatus(Long userId, String status) {
        Order.OrderStatus orderStatus;
        try {
            orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        return orderRepository.findByUserIdAndStatusOrderByOrderDateDesc(userId, orderStatus).stream()
                .map(OrderResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        if (!order.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Order does not belong to this user");
        }
        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Order is already cancelled");
        }
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new IllegalArgumentException("Only pending orders can be cancelled");
        }
        order.setStatus(Order.OrderStatus.CANCELLED);

        for (OrderItem item : order.getItems()) {
            item.getProduct().setStock(item.getProduct().getStock() + item.getQuantity());
            productRepository.save(item.getProduct());
        }

        orderRepository.save(order);
    }
}

package com.generic.e_commerce.cart.service;

import com.generic.e_commerce.cart.dto.request.AddToCartRequest;
import com.generic.e_commerce.cart.dto.request.UpdateCartItemRequest;
import com.generic.e_commerce.cart.dto.response.CartItemResponse;
import com.generic.e_commerce.cart.model.CartItem;
import com.generic.e_commerce.cart.repository.CartItemRepository;
import com.generic.e_commerce.product.model.Product;
import com.generic.e_commerce.product.repository.ProductRepository;
import com.generic.e_commerce.shared.exception.ResourceNotFoundException;
import com.generic.e_commerce.shared.exception.UnauthorizedException;
import com.generic.e_commerce.user.model.User;
import com.generic.e_commerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public CartItemResponse addToCart(Long userId, AddToCartRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        if (request.getQuantity() > product.getStock()) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        CartItem existing = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId()).orElse(null);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            if (existing.getQuantity() > product.getStock()) {
                throw new IllegalArgumentException("Insufficient stock");
            }
            existing = cartItemRepository.save(existing);
            return CartItemResponse.fromEntity(existing);
        }

        CartItem cartItem = CartItem.builder()
                .user(user)
                .product(product)
                .quantity(request.getQuantity())
                .build();
        cartItem = cartItemRepository.save(cartItem);
        return CartItemResponse.fromEntity(cartItem);
    }

    @Override
    public CartItemResponse updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequest request) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        if (!cartItem.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Cart item does not belong to this user");
        }

        if (request.getQuantity() > cartItem.getProduct().getStock()) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        cartItem.setQuantity(request.getQuantity());
        cartItem = cartItemRepository.save(cartItem);
        return CartItemResponse.fromEntity(cartItem);
    }

    @Override
    public List<CartItemResponse> getCartItems(Long userId) {
        return cartItemRepository.findByUserId(userId).stream()
                .map(CartItemResponse::fromEntity)
                .toList();
    }

    @Override
    public void removeCartItem(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));
        if (!cartItem.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Cart item does not belong to this user");
        }
        cartItemRepository.delete(cartItem);
    }

    @Override
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}

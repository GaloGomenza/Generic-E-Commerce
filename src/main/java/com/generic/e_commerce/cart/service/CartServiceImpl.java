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

import java.math.BigDecimal;
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
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con el id: " + userId));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto con el id: " + request.getProductId()));

        if (request.getQuantity() > product.getStock()) {
            throw new IllegalArgumentException("Stock insuficiente.");
        }

        CartItem existing = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId()).orElse(null);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            if (existing.getQuantity() > product.getStock()) {
                throw new IllegalArgumentException("Stock insuficiente");
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
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el item de carrito con el id: " + cartItemId));

        if (!cartItem.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("El item del carrito no le pertenece a este usuario.");
        }

        if (request.getQuantity() > cartItem.getProduct().getStock()) {
            throw new IllegalArgumentException("Stock insuficiente.");
        }

        cartItem.setQuantity(request.getQuantity());
        cartItem = cartItemRepository.save(cartItem);
        return CartItemResponse.fromEntity(cartItem);
    }

    @Override
    public List<CartItemResponse> getCartItems(Long userId, String productName, BigDecimal minPrice, BigDecimal maxPrice) {
        return cartItemRepository.findByUserId(userId).stream()
                .filter(item -> productName == null || productName.isBlank()
                        || item.getProduct().getName().toLowerCase().contains(productName.toLowerCase()))
                .filter(item -> minPrice == null || item.getProduct().getPrice().compareTo(minPrice) >= 0)
                .filter(item -> maxPrice == null || item.getProduct().getPrice().compareTo(maxPrice) <= 0)
                .map(CartItemResponse::fromEntity)
                .toList();
    }

    @Override
    public void removeCartItem(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el item de carrito con el id: " + cartItemId));
        if (!cartItem.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("El item del carrito no le pertenece a este usuario.");
        }
        cartItemRepository.delete(cartItem);
    }

    @Override
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}

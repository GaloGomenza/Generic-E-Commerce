package com.generic.e_commerce.cart.service;

import com.generic.e_commerce.cart.dto.request.AddToCartRequest;
import com.generic.e_commerce.cart.dto.request.UpdateCartItemRequest;
import com.generic.e_commerce.cart.dto.response.CartItemResponse;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    CartItemResponse addToCart(Long userId, AddToCartRequest request);
    CartItemResponse updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequest request);
    List<CartItemResponse> getCartItems(Long userId, String productName, BigDecimal minPrice, BigDecimal maxPrice);
    void removeCartItem(Long userId, Long cartItemId);
    void clearCart(Long userId);
}

package com.generic.e_commerce.cart.service;

import com.ecommerce.cart.dto.request.AddToCartRequest;
import com.ecommerce.cart.dto.request.UpdateCartItemRequest;
import com.ecommerce.cart.dto.response.CartItemResponse;

import java.util.List;

public interface CartService {
    CartItemResponse addToCart(Long userId, AddToCartRequest request);
    CartItemResponse updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequest request);
    List<CartItemResponse> getCartItems(Long userId);
    void removeCartItem(Long userId, Long cartItemId);
    void clearCart(Long userId);
}

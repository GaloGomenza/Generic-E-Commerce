package com.generic.e_commerce.cart.controller;

import com.generic.e_commerce.cart.dto.request.AddToCartRequest;
import com.generic.e_commerce.cart.dto.request.UpdateCartItemRequest;
import com.generic.e_commerce.cart.dto.response.CartItemResponse;
import com.generic.e_commerce.cart.service.CartService;
import com.generic.e_commerce.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartItemResponse> addToCart(
            @RequestHeader("X-User-Email") String email,
            @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addToCart(1L, request));
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCartItems(@RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(cartService.getCartItems(1L));
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateCartItem(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateCartItem(1L, cartItemId, request));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse> removeCartItem(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long cartItemId) {
        cartService.removeCartItem(1L, cartItemId);
        return ResponseEntity.ok(new ApiResponse("Cart item removed successfully"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> clearCart(@RequestHeader("X-User-Email") String email) {
        cartService.clearCart(1L);
        return ResponseEntity.ok(new ApiResponse("Cart cleared successfully"));
    }
}

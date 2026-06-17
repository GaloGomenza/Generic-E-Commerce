package com.generic.e_commerce.cart.controller;

import com.generic.e_commerce.cart.dto.request.AddToCartRequest;
import com.generic.e_commerce.cart.dto.request.UpdateCartItemRequest;
import com.generic.e_commerce.cart.dto.response.CartItemResponse;
import com.generic.e_commerce.cart.service.CartService;
import com.generic.e_commerce.shared.dto.ApiResponse;
import com.generic.e_commerce.shared.exception.ResourceNotFoundException;
import com.generic.e_commerce.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    private Long getUserId(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con el email: " + email))
                .getId();
    }

    @PostMapping
    public ResponseEntity<CartItemResponse> addToCart(
            @RequestHeader("X-User-Email") String email,
            @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addToCart(getUserId(email), request));
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCartItems(
            @RequestHeader("X-User-Email") String email,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        return ResponseEntity.ok(cartService.getCartItems(getUserId(email), productName, minPrice, maxPrice));
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateCartItem(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateCartItem(getUserId(email), cartItemId, request));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse> removeCartItem(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long cartItemId) {
        cartService.removeCartItem(getUserId(email), cartItemId);
        return ResponseEntity.ok(new ApiResponse("El item del carrito se borró exitosamente."));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> clearCart(@RequestHeader("X-User-Email") String email) {
        cartService.clearCart(getUserId(email));
        return ResponseEntity.ok(new ApiResponse("Se vació el carrito exitosamente."));
    }
}

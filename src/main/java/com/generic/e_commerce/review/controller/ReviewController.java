package com.generic.e_commerce.review.controller;

import com.generic.e_commerce.review.dto.request.ReviewRequest;
import com.generic.e_commerce.review.dto.response.ReviewResponse;
import com.generic.e_commerce.review.service.ReviewService;
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
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository;

    private Long getUserId(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con el email: " + email))
                .getId();
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @RequestHeader("X-User-Email") String email,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(getUserId(email), request));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByProduct(
            @PathVariable Long productId,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) Integer maxRating) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId, minRating, maxRating));
    }

    @GetMapping("/user")
    public ResponseEntity<List<ReviewResponse>> getReviewsByUser(@RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(getUserId(email)));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(getUserId(email), reviewId, request));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse> deleteReview(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(getUserId(email), reviewId);
        return ResponseEntity.ok(new ApiResponse("La reseña se borró exitosamente."));
    }
}

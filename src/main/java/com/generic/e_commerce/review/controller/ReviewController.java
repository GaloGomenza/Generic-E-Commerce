package com.generic.e_commerce.review.controller;

import com.ecommerce.review.dto.request.ReviewRequest;
import com.ecommerce.review.dto.response.ReviewResponse;
import com.ecommerce.review.service.ReviewService;
import com.ecommerce.shared.dto.ApiResponse;
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

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @RequestHeader("X-User-Email") String email,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(1L, request));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }

    @GetMapping("/user")
    public ResponseEntity<List<ReviewResponse>> getReviewsByUser(@RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(1L));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(1L, reviewId, request));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse> deleteReview(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(1L, reviewId);
        return ResponseEntity.ok(new ApiResponse("Review deleted successfully"));
    }
}

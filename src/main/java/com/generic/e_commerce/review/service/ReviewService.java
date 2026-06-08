package com.generic.e_commerce.review.service;

import com.ecommerce.review.dto.request.ReviewRequest;
import com.ecommerce.review.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(Long userId, ReviewRequest request);
    ReviewResponse updateReview(Long userId, Long reviewId, ReviewRequest request);
    void deleteReview(Long userId, Long reviewId);
    List<ReviewResponse> getReviewsByProduct(Long productId);
    List<ReviewResponse> getReviewsByUser(Long userId);
}

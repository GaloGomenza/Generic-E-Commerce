package com.generic.e_commerce.review.service;

import com.generic.e_commerce.review.dto.request.ReviewRequest;
import com.generic.e_commerce.review.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(Long userId, ReviewRequest request);
    ReviewResponse updateReview(Long userId, Long reviewId, ReviewRequest request);
    void deleteReview(Long userId, Long reviewId);
    List<ReviewResponse> getReviewsByProduct(Long productId, Integer minRating, Integer maxRating);
    List<ReviewResponse> getReviewsByUser(Long userId);
}

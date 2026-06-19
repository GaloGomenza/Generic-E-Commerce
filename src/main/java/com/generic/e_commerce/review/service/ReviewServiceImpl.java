package com.generic.e_commerce.review.service;

import com.generic.e_commerce.order.model.Order;
import com.generic.e_commerce.order.repository.OrderRepository;
import com.generic.e_commerce.product.model.Product;
import com.generic.e_commerce.product.repository.ProductRepository;
import com.generic.e_commerce.review.dto.request.ReviewRequest;
import com.generic.e_commerce.review.dto.response.ReviewResponse;
import com.generic.e_commerce.review.model.Review;
import com.generic.e_commerce.review.repository.ReviewRepository;
import com.generic.e_commerce.shared.exception.ResourceNotFoundException;
import com.generic.e_commerce.shared.exception.UnauthorizedException;
import com.generic.e_commerce.user.model.User;
import com.generic.e_commerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    public ReviewResponse createReview(Long userId, ReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con el id: " + userId));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto con el id: " + request.getProductId()));

        List<Order> userOrders = orderRepository.findByUserIdOrderByOrderDateDesc(userId);
        boolean hasPurchased = userOrders.stream()
                .filter(order -> order.getStatus() != Order.OrderStatus.CANCELLED)
                .anyMatch(order -> order.getItems().stream()
                        .anyMatch(item -> item.getProduct().getId().equals(request.getProductId())));

        if (!hasPurchased)
            throw new IllegalArgumentException("Se debe comprar el producto antes de reseñarlo.");

        if (reviewRepository.existsByUserIdAndProductId(userId, request.getProductId()))
            throw new IllegalArgumentException("Ya se hizo una reseña de este producto.");

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        review = reviewRepository.save(review);
        return ReviewResponse.fromEntity(review);
    }

    @Override
    public ReviewResponse updateReview(Long userId, Long reviewId, ReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la reseña con el id: " + reviewId));
        if (!review.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("La reseña no le pertenece a este usuario.");
        }
        if (request.getRating() != null) review.setRating(request.getRating());
        if (request.getComment() != null) review.setComment(request.getComment());
        review = reviewRepository.save(review);
        return ReviewResponse.fromEntity(review);
    }

    @Override
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la reseña con el id: " + reviewId));
        if (!review.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("La reseña no le pertenece a este usuario.");
        }
        reviewRepository.delete(review);
    }

    @Override
    public List<ReviewResponse> getReviewsByProduct(Long productId, Integer minRating, Integer maxRating) {
        return reviewRepository.findByProductId(productId).stream()
                .filter(review -> minRating == null || review.getRating() >= minRating)
                .filter(review -> maxRating == null || review.getRating() <= maxRating)
                .map(ReviewResponse::fromEntity)
                .toList();
    }

    @Override
    public List<ReviewResponse> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId).stream()
                .map(ReviewResponse::fromEntity)
                .toList();
    }
}

package com.generic.e_commerce.order.repository;

import com.generic.e_commerce.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
    List<Order> findByUserIdAndStatusOrderByOrderDateDesc(Long userId, Order.OrderStatus status);
}

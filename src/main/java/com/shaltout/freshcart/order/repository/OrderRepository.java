package com.shaltout.freshcart.order.repository;

import com.shaltout.freshcart.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    Page<Order> findByUserId(String userId, Pageable pageable);

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByUserIdAndStatus(String userId, Order.OrderStatus status);

    long countByUserId(String userId);
}
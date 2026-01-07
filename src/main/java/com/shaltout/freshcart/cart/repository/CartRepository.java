package com.shaltout.freshcart.cart.repository;

import com.shaltout.freshcart.cart.entity.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends MongoRepository<Cart, String> {

    Optional<Cart> findByUserIdAndStatus(String userId, Cart.CartStatus status);

    @Query("{'status': ?0, 'updatedAt': {$lt: ?1}}")
    List<Cart> findAllByStatusAndUpdatedAtBefore(Cart.CartStatus status, LocalDateTime date);

    void deleteByUserIdAndStatus(String userId, Cart.CartStatus status);
}
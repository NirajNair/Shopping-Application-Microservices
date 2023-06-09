package com.dexter.cartservice.repository;

import com.dexter.cartservice.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface CartRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findByUserId(String userId);
    Optional<Cart> findByUserIdWithCartItems(String userId);
}

package com.shaltout.freshcart.product.repository;

import com.shaltout.freshcart.product.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

    Optional<Category> findBySlug(String slug);

    Page<Category> findByActiveTrue(Pageable pageable);

    boolean existsByName(String name);
}
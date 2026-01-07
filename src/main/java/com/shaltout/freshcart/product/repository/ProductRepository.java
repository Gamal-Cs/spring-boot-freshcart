package com.shaltout.freshcart.product.repository;

import com.shaltout.freshcart.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    Optional<Product> findBySlug(String slug);

    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByCategoryIdAndActiveTrue(String categoryId, Pageable pageable);

    Page<Product> findByBrandIdAndActiveTrue(String brandId, Pageable pageable);

    @Query("{'$and': [{'active': true}, {'price': {'$gte': ?0, '$lte': ?1}}]}")
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    @Query("{'$and': [{'active': true}, {'title': {$regex: ?0, $options: 'i'}}]}")
    Page<Product> searchByTitle(String keyword, Pageable pageable);

    List<Product> findByIdIn(List<String> ids);
}
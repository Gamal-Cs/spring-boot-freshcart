package com.shaltout.freshcart.product.repository;

import com.shaltout.freshcart.product.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends MongoRepository<Brand, String> {

    Optional<Brand> findBySlug(String slug);

    Page<Brand> findByActiveTrue(Pageable pageable);

    boolean existsByName(String name);
}
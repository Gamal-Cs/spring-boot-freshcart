package com.shaltout.freshcart.product.service;

import com.shaltout.freshcart.common.exception.BusinessException;
import com.shaltout.freshcart.common.exception.ResourceNotFoundException;
import com.shaltout.freshcart.product.dto.ProductRequest;
import com.shaltout.freshcart.product.dto.ProductResponse;
import com.shaltout.freshcart.product.entity.Product;
import com.shaltout.freshcart.product.mapper.ProductMapper;
import com.shaltout.freshcart.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final ProductMapper productMapper;

    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating product: {}", request.getTitle());

        Product product = productMapper.toEntity(request);
        product.setCategory(categoryService.getCategoryEntityById(request.getCategoryId()));
        product.setBrand(brandService.getBrandEntityById(request.getBrandId()));
        product.setActive(true);
        product.setSold(0);
        product.setRatingsQuantity(0);
        product.setRatingsAverage(0.0);

        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Cacheable(value = "products", key = "#id")
    public ProductResponse getProductById(String id) {
        log.info("Fetching product by id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toResponse(product);
    }

    public ProductResponse getProductBySlug(String slug) {
        log.info("Fetching product by slug: {}", slug);
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with slug: " + slug));
        return productMapper.toResponse(product);
    }

    @Cacheable(value = "products", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        log.info("Fetching all products");
        return productRepository.findByActiveTrue(pageable)
                .map(productMapper::toResponse);
    }

    @CacheEvict(value = "products", key = "#id")
    public ProductResponse updateProduct(String id, ProductRequest request) {
        log.info("Updating product: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        productMapper.updateEntity(request, product);

        if (request.getCategoryId() != null) {
            product.setCategory(categoryService.getCategoryEntityById(request.getCategoryId()));
        }

        if (request.getBrandId() != null) {
            product.setBrand(brandService.getBrandEntityById(request.getBrandId()));
        }

        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(String id) {
        log.info("Deleting product: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setActive(false);
        productRepository.save(product);
    }

    public Page<ProductResponse> getProductsByCategory(String categoryId, Pageable pageable) {
        log.info("Fetching products by category: {}", categoryId);
        return productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable)
                .map(productMapper::toResponse);
    }

    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        log.info("Searching products with keyword: {}", keyword);
        return productRepository.searchByTitle(keyword, pageable)
                .map(productMapper::toResponse);
    }

    public Page<ProductResponse> filterByPrice(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.info("Filtering products by price range: {} - {}", minPrice, maxPrice);
        return productRepository.findByPriceBetween(minPrice, maxPrice, pageable)
                .map(productMapper::toResponse);
    }

    // Internal method used by other services
    public Product getProductEntityById(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }

    public void updateProductStock(String productId, Integer quantity) {
        log.info("Updating stock for product: {} by quantity: {}", productId, quantity);
        Product product = getProductEntityById(productId);

        if (product.getQuantity() < quantity) {
            throw new BusinessException("Insufficient stock for product: " + product.getTitle());
        }

        product.setQuantity(product.getQuantity() - quantity);
        product.setSold(product.getSold() + quantity);
        productRepository.save(product);
    }
}
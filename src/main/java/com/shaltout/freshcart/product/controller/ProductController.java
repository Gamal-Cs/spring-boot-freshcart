package com.shaltout.freshcart.product.controller;

import com.shaltout.freshcart.common.dto.ApiResponse;
import com.shaltout.freshcart.product.dto.ProductRequest;
import com.shaltout.freshcart.product.dto.ProductResponse;
import com.shaltout.freshcart.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management APIs")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new product")
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ApiResponse.success(productService.createProduct(request), "Product created successfully");
    }

    @GetMapping
    @Operation(summary = "Get all products")
    public ApiResponse<Page<ProductResponse>> getAllProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(productService.getAllProducts(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ApiResponse<ProductResponse> getProductById(@PathVariable String id) {
        return ApiResponse.success(productService.getProductById(id));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get product by slug")
    public ApiResponse<ProductResponse> getProductBySlug(@PathVariable String slug) {
        return ApiResponse.success(productService.getProductBySlug(slug));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update product")
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductRequest request) {
        return ApiResponse.success(productService.updateProduct(id, request), "Product updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete product")
    public ApiResponse<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ApiResponse.success(null, "Product deleted successfully");
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category")
    public ApiResponse<Page<ProductResponse>> getProductsByCategory(
            @PathVariable String categoryId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(productService.getProductsByCategory(categoryId, pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products")
    public ApiResponse<Page<ProductResponse>> searchProducts(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(productService.searchProducts(keyword, pageable));
    }

    @GetMapping("/filter/price")
    @Operation(summary = "Filter products by price range")
    public ApiResponse<Page<ProductResponse>> filterByPrice(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(productService.filterByPrice(minPrice, maxPrice, pageable));
    }
}
package com.shaltout.freshcart.product.controller;

import com.shaltout.freshcart.common.dto.ApiResponse;
import com.shaltout.freshcart.product.dto.BrandRequest;
import com.shaltout.freshcart.product.dto.BrandResponse;
import com.shaltout.freshcart.product.service.BrandService;
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

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
@Tag(name = "Brands", description = "Brand management APIs")
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new brand")
    public ApiResponse<BrandResponse> createBrand(@Valid @RequestBody BrandRequest request) {
        return ApiResponse.success(brandService.createBrand(request), "Brand created successfully");
    }

    @GetMapping
    @Operation(summary = "Get all brands")
    public ApiResponse<Page<BrandResponse>> getAllBrands(
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(brandService.getAllBrands(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get brand by ID")
    public ApiResponse<BrandResponse> getBrandById(@PathVariable String id) {
        return ApiResponse.success(brandService.getBrandById(id));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get brand by slug")
    public ApiResponse<BrandResponse> getBrandBySlug(@PathVariable String slug) {
        return ApiResponse.success(brandService.getBrandBySlug(slug));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update brand")
    public ApiResponse<BrandResponse> updateBrand(
            @PathVariable String id,
            @Valid @RequestBody BrandRequest request) {
        return ApiResponse.success(brandService.updateBrand(id, request), "Brand updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete brand")
    public ApiResponse<Void> deleteBrand(@PathVariable String id) {
        brandService.deleteBrand(id);
        return ApiResponse.success(null, "Brand deleted successfully");
    }
}
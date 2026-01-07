package com.shaltout.freshcart.product.controller;

import com.shaltout.freshcart.common.dto.ApiResponse;
import com.shaltout.freshcart.product.dto.CategoryRequest;
import com.shaltout.freshcart.product.dto.CategoryResponse;
import com.shaltout.freshcart.product.service.CategoryService;
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
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management APIs")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new category")
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ApiResponse.success(categoryService.createCategory(request), "Category created successfully");
    }

    @GetMapping
    @Operation(summary = "Get all categories")
    public ApiResponse<Page<CategoryResponse>> getAllCategories(
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(categoryService.getAllCategories(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable String id) {
        return ApiResponse.success(categoryService.getCategoryById(id));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get category by slug")
    public ApiResponse<CategoryResponse> getCategoryBySlug(@PathVariable String slug) {
        return ApiResponse.success(categoryService.getCategoryBySlug(slug));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update category")
    public ApiResponse<CategoryResponse> updateCategory(
            @PathVariable String id,
            @Valid @RequestBody CategoryRequest request) {
        return ApiResponse.success(categoryService.updateCategory(id, request), "Category updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete category")
    public ApiResponse<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success(null, "Category deleted successfully");
    }
}
package com.shaltout.freshcart.product.service;

import com.shaltout.freshcart.common.exception.BusinessException;
import com.shaltout.freshcart.common.exception.ResourceNotFoundException;
import com.shaltout.freshcart.common.util.SlugUtil;
import com.shaltout.freshcart.product.dto.CategoryRequest;
import com.shaltout.freshcart.product.dto.CategoryResponse;
import com.shaltout.freshcart.product.entity.Category;
import com.shaltout.freshcart.product.mapper.CategoryMapper;
import com.shaltout.freshcart.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("Creating category: {}", request.getName());

        if (categoryRepository.existsByName(request.getName())) {
            throw new BusinessException("Category with this name already exists");
        }

        Category category = categoryMapper.toEntity(request);
        category.setSlug(SlugUtil.toSlug(request.getName()));
        category.setActive(true);

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    @Cacheable(value = "categories", key = "#id")
    public CategoryResponse getCategoryById(String id) {
        log.info("Fetching category by id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return categoryMapper.toResponse(category);
    }

    public CategoryResponse getCategoryBySlug(String slug) {
        log.info("Fetching category by slug: {}", slug);
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
        return categoryMapper.toResponse(category);
    }

    @Cacheable(value = "categories", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        log.info("Fetching all categories");
        return categoryRepository.findByActiveTrue(pageable)
                .map(categoryMapper::toResponse);
    }

    @CacheEvict(value = "categories", key = "#id")
    public CategoryResponse updateCategory(String id, CategoryRequest request) {
        log.info("Updating category: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        categoryMapper.updateEntity(request, category);

        if (request.getName() != null && !request.getName().equals(category.getName())) {
            category.setSlug(SlugUtil.toSlug(request.getName()));
        }

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(updatedCategory);
    }

    @CacheEvict(value = "categories", key = "#id")
    public void deleteCategory(String id) {
        log.info("Deleting category: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        category.setActive(false);
        categoryRepository.save(category);
    }

    // Internal method used by ProductService
    Category getCategoryEntityById(String categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }
}
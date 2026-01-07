package com.shaltout.freshcart.product.service;

import com.shaltout.freshcart.common.exception.BusinessException;
import com.shaltout.freshcart.common.exception.ResourceNotFoundException;
import com.shaltout.freshcart.common.util.SlugUtil;
import com.shaltout.freshcart.product.dto.BrandRequest;
import com.shaltout.freshcart.product.dto.BrandResponse;
import com.shaltout.freshcart.product.entity.Brand;
import com.shaltout.freshcart.product.mapper.BrandMapper;
import com.shaltout.freshcart.product.repository.BrandRepository;
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
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public BrandResponse createBrand(BrandRequest request) {
        log.info("Creating brand: {}", request.getName());

        if (brandRepository.existsByName(request.getName())) {
            throw new BusinessException("Brand with this name already exists");
        }

        Brand brand = brandMapper.toEntity(request);
        brand.setSlug(SlugUtil.toSlug(request.getName()));
        brand.setActive(true);

        Brand savedBrand = brandRepository.save(brand);
        return brandMapper.toResponse(savedBrand);
    }

    @Cacheable(value = "brands", key = "#id")
    public BrandResponse getBrandById(String id) {
        log.info("Fetching brand by id: {}", id);
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));
        return brandMapper.toResponse(brand);
    }

    public BrandResponse getBrandBySlug(String slug) {
        log.info("Fetching brand by slug: {}", slug);
        Brand brand = brandRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with slug: " + slug));
        return brandMapper.toResponse(brand);
    }

    @Cacheable(value = "brands", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<BrandResponse> getAllBrands(Pageable pageable) {
        log.info("Fetching all brands");
        return brandRepository.findByActiveTrue(pageable)
                .map(brandMapper::toResponse);
    }

    @CacheEvict(value = "brands", key = "#id")
    public BrandResponse updateBrand(String id, BrandRequest request) {
        log.info("Updating brand: {}", id);

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));

        brandMapper.updateEntity(request, brand);

        if (request.getName() != null && !request.getName().equals(brand.getName())) {
            brand.setSlug(SlugUtil.toSlug(request.getName()));
        }

        Brand updatedBrand = brandRepository.save(brand);
        return brandMapper.toResponse(updatedBrand);
    }

    @CacheEvict(value = "brands", key = "#id")
    public void deleteBrand(String id) {
        log.info("Deleting brand: {}", id);
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));
        brand.setActive(false);
        brandRepository.save(brand);
    }

    // Internal method used by ProductService
    Brand getBrandEntityById(String brandId) {
        return brandRepository.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + brandId));
    }
}
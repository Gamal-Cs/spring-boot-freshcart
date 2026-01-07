package com.shaltout.freshcart.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private String id;
    private String title;
    private String slug;
    private String description;
    private BigDecimal price;
    private String imageCover;
    private List<String> images;
    private CategoryResponse category;
    private BrandResponse brand;
    private List<ProductAttributeDto> attributes;
    private Double ratingsAverage;
    private Integer ratingsQuantity;
    private Integer sold;
    private Integer quantity;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductAttributeDto {
        private String key;
        private String value;
        private String unit;
    }
}
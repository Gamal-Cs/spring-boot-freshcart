package com.shaltout.freshcart.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponse {
    private String id;
    private String name;
    private String slug;
    private String description;
    private String image;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
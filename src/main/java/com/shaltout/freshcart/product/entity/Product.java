package com.shaltout.freshcart.product.entity;

import com.shaltout.freshcart.common.entity.BaseEntity;
import com.shaltout.freshcart.common.util.SlugUtil;
import jakarta.persistence.PrePersist;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity {

    @Indexed
    private String title;

    @Indexed(unique = true)
    private String slug;

    private String description;

    private BigDecimal price;

    private String imageCover;

    @Builder.Default
    private List<String> images = new ArrayList<>();

    @DBRef
    private Category category;

    @DBRef
    private Brand brand;

    @Builder.Default
    private List<ProductAttribute> attributes = new ArrayList<>();

    @Builder.Default
    private Double ratingsAverage = 0.0;

    @Builder.Default
    private Integer ratingsQuantity = 0;

    @Builder.Default
    private Integer sold = 0;

    private Integer quantity;

    @Builder.Default
    private Boolean active = true;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductAttribute {
        private String key;
        private String value;
        private String unit;
    }

    @PrePersist
    public void prePersist() {
        if (slug == null) {
            slug = SlugUtil.toSlug(title);
        }
    }
}
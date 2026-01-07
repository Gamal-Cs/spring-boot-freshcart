package com.shaltout.freshcart.product.entity;

import com.shaltout.freshcart.common.entity.BaseEntity;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseEntity {

    @Indexed
    private String name;

    @Indexed(unique = true)
    private String slug;

    private String description;

    private String image;

    private Boolean active;
}
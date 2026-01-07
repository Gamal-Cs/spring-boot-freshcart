package com.shaltout.freshcart.product.entity;

import com.shaltout.freshcart.common.entity.BaseEntity;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "brands")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Brand extends BaseEntity {

    @Indexed
    private String name;

    @Indexed(unique = true)
    private String slug;

    private String description;

    private String image;

    private Boolean active;
}
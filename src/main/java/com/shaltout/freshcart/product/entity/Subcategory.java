package com.shaltout.freshcart.product.entity;

import com.shaltout.freshcart.common.entity.BaseEntity;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "subcategories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Subcategory extends BaseEntity {

    @Indexed
    private String name;

    @Indexed(unique = true)
    private String slug;

    @DBRef
    private Category category;

    private Boolean active;
}
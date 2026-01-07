package com.shaltout.freshcart.product.mapper;

import com.shaltout.freshcart.product.dto.ProductRequest;
import com.shaltout.freshcart.product.dto.ProductResponse;
import com.shaltout.freshcart.product.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "ratingsAverage", ignore = true)
    @Mapping(target = "ratingsQuantity", ignore = true)
    @Mapping(target = "sold", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Product toEntity(ProductRequest request);

    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "ratingsAverage", ignore = true)
    @Mapping(target = "ratingsQuantity", ignore = true)
    @Mapping(target = "sold", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    void updateEntity(ProductRequest request, @MappingTarget Product product);
}
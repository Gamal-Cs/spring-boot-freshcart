package com.shaltout.freshcart.cart.mapper;

import com.shaltout.freshcart.cart.dto.CartResponse;
import com.shaltout.freshcart.cart.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "status", expression = "java(cart.getStatus().name())")
    CartResponse toResponse(Cart cart);

    default CartResponse.CartItemDto toCartItemDto(Cart.CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }
        return CartResponse.CartItemDto.builder()
                .productId(cartItem.getProductId())
                .productTitle(cartItem.getProductTitle())
                .productImage(cartItem.getProductImage())
                .price(cartItem.getPrice())
                .quantity(cartItem.getQuantity())
                .totalPrice(cartItem.getTotalPrice())
                .build();
    }

    default CartResponse.CartSummaryDto toCartSummaryDto(Cart.CartSummary cartSummary) {
        if (cartSummary == null) {
            return null;
        }
        return CartResponse.CartSummaryDto.builder()
                .totalItems(cartSummary.getTotalItems())
                .totalPrice(cartSummary.getTotalPrice())
                .discount(cartSummary.getDiscount())
                .shipping(cartSummary.getShipping())
                .tax(cartSummary.getTax())
                .grandTotal(cartSummary.getGrandTotal())
                .build();
    }
}
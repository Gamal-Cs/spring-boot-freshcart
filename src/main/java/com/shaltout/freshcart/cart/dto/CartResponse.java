package com.shaltout.freshcart.cart.dto;

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
public class CartResponse {
    private String id;
    private String userId;
    private String status;
    private List<CartItemDto> items;
    private CartSummaryDto summary;
    private String couponCode;
    private BigDecimal discount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemDto {
        private String productId;
        private String productTitle;
        private String productImage;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal totalPrice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartSummaryDto {
        private Integer totalItems;
        private BigDecimal totalPrice;
        private BigDecimal discount;
        private BigDecimal shipping;
        private BigDecimal tax;
        private BigDecimal grandTotal;
    }
}
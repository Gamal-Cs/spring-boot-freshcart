package com.shaltout.freshcart.cart.entity;

import com.shaltout.freshcart.common.entity.BaseEntity;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "carts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Cart extends BaseEntity {

    private String userId;

    private CartStatus status;

    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    private CartSummary summary;

    private String couponCode;

    private BigDecimal discount;

    private LocalDateTime expiresAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItem {
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
    public static class CartSummary {
        private Integer totalItems;
        private BigDecimal totalPrice;
        private BigDecimal discount;
        private BigDecimal shipping;
        private BigDecimal tax;
        private BigDecimal grandTotal;
    }

    public enum CartStatus {
        ACTIVE, ABANDONED, CONVERTED, EXPIRED
    }
}
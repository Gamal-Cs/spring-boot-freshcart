package com.shaltout.freshcart.order.dto;

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
public class OrderResponse {
    private String id;
    private String orderNumber;
    private String userId;
    private String status;
    private ShippingAddressDto shippingAddress;
    private List<OrderItemDto> items;
    private OrderSummaryDto summary;
    private PaymentInfoDto paymentInfo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingAddressDto {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String address;
        private String city;
        private String state;
        private String country;
        private String zipCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto {
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
    public static class OrderSummaryDto {
        private Integer totalItems;
        private BigDecimal subtotal;
        private BigDecimal discount;
        private BigDecimal shipping;
        private BigDecimal tax;
        private BigDecimal total;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentInfoDto {
        private String method;
        private String transactionId;
        private String status;
        private LocalDateTime paidAt;
    }
}
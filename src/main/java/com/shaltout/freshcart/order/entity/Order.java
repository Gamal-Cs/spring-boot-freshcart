package com.shaltout.freshcart.order.entity;

import com.shaltout.freshcart.common.entity.BaseEntity;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseEntity {

    private String orderNumber;

    private String userId;

    private OrderStatus status;

    private ShippingAddress shippingAddress;

    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    private OrderSummary summary;

    private PaymentInfo paymentInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
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
    public static class ShippingAddress {
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
    public static class OrderSummary {
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
    public static class PaymentInfo {
        private String method;
        private String transactionId;
        private PaymentStatus status;
        private LocalDateTime paidAt;
    }

    public enum OrderStatus {
        PENDING,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        REFUNDED
    }

    public enum PaymentStatus {
        PENDING,
        PAID,
        FAILED,
        REFUNDED
    }
}
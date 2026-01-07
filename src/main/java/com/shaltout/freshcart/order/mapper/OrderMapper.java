package com.shaltout.freshcart.order.mapper;

import com.shaltout.freshcart.order.dto.OrderResponse;
import com.shaltout.freshcart.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "status", expression = "java(order.getStatus().name())")
    OrderResponse toResponse(Order order);

    default OrderResponse.ShippingAddressDto toShippingAddressDto(Order.ShippingAddress shippingAddress) {
        if (shippingAddress == null) {
            return null;
        }
        return OrderResponse.ShippingAddressDto.builder()
                .firstName(shippingAddress.getFirstName())
                .lastName(shippingAddress.getLastName())
                .email(shippingAddress.getEmail())
                .phone(shippingAddress.getPhone())
                .address(shippingAddress.getAddress())
                .city(shippingAddress.getCity())
                .state(shippingAddress.getState())
                .country(shippingAddress.getCountry())
                .zipCode(shippingAddress.getZipCode())
                .build();
    }

    default OrderResponse.OrderItemDto toOrderItemDto(Order.OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        return OrderResponse.OrderItemDto.builder()
                .productId(orderItem.getProductId())
                .productTitle(orderItem.getProductTitle())
                .productImage(orderItem.getProductImage())
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .totalPrice(orderItem.getTotalPrice())
                .build();
    }

    default OrderResponse.OrderSummaryDto toOrderSummaryDto(Order.OrderSummary orderSummary) {
        if (orderSummary == null) {
            return null;
        }
        return OrderResponse.OrderSummaryDto.builder()
                .totalItems(orderSummary.getTotalItems())
                .subtotal(orderSummary.getSubtotal())
                .discount(orderSummary.getDiscount())
                .shipping(orderSummary.getShipping())
                .tax(orderSummary.getTax())
                .total(orderSummary.getTotal())
                .build();
    }

    default OrderResponse.PaymentInfoDto toPaymentInfoDto(Order.PaymentInfo paymentInfo) {
        if (paymentInfo == null) {
            return null;
        }
        return OrderResponse.PaymentInfoDto.builder()
                .method(paymentInfo.getMethod())
                .transactionId(paymentInfo.getTransactionId())
                .status(paymentInfo.getStatus().name())
                .paidAt(paymentInfo.getPaidAt())
                .build();
    }
}
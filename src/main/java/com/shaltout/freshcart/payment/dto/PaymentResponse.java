package com.shaltout.freshcart.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String transactionId;
    private String status;
    private String message;
    private LocalDateTime timestamp;
    private String paymentGateway;
    private String orderId;
}
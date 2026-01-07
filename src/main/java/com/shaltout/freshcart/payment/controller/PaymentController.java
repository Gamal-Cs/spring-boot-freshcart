package com.shaltout.freshcart.payment.controller;

import com.shaltout.freshcart.common.dto.ApiResponse;
import com.shaltout.freshcart.payment.dto.PaymentRequest;
import com.shaltout.freshcart.payment.dto.PaymentResponse;
import com.shaltout.freshcart.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing APIs")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    @Operation(summary = "Process payment")
    public ApiResponse<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        String transactionId = paymentService.processPayment(
                request.getOrderId(),
                request.getAmount(),
                request.getPaymentMethod()
        );

        PaymentResponse response = PaymentResponse.builder()
                .transactionId(transactionId)
                .status("SUCCESS")
                .message("Payment processed successfully")
                .timestamp(LocalDateTime.now())
                .paymentGateway(request.getPaymentGateway())
                .orderId(request.getOrderId())
                .build();

        return ApiResponse.success(response, "Payment processed successfully");
    }

    @PostMapping("/verify/{transactionId}")
    @Operation(summary = "Verify payment")
    public ApiResponse<PaymentResponse> verifyPayment(@PathVariable String transactionId) {
        boolean verified = paymentService.verifyPayment(transactionId);

        PaymentResponse response = PaymentResponse.builder()
                .transactionId(transactionId)
                .status(verified ? "VERIFIED" : "FAILED")
                .message(verified ? "Payment verified" : "Payment verification failed")
                .timestamp(LocalDateTime.now())
                .build();

        return ApiResponse.success(response);
    }
}
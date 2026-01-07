package com.shaltout.freshcart.order.controller;

import com.shaltout.freshcart.common.dto.ApiResponse;
import com.shaltout.freshcart.order.dto.OrderRequest;
import com.shaltout.freshcart.order.dto.OrderResponse;
import com.shaltout.freshcart.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create a new order from cart")
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        return ApiResponse.success(orderService.createOrder(request), "Order created successfully");
    }

    @GetMapping
    @Operation(summary = "Get user's orders")
    public ApiResponse<Page<OrderResponse>> getUserOrders(
            @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.success(orderService.getUserOrders(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable String id) {
        return ApiResponse.success(orderService.getOrderById(id));
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by order number")
    public ApiResponse<OrderResponse> getOrderByOrderNumber(@PathVariable String orderNumber) {
        return ApiResponse.success(orderService.getOrderByOrderNumber(orderNumber));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel order")
    public ApiResponse<OrderResponse> cancelOrder(@PathVariable String id) {
        return ApiResponse.success(orderService.cancelOrder(id), "Order cancelled successfully");
    }
}
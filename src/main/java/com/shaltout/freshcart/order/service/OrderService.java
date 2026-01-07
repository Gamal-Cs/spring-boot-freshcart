package com.shaltout.freshcart.order.service;

import com.shaltout.freshcart.cart.dto.CartResponse;
import com.shaltout.freshcart.cart.service.CartService;
import com.shaltout.freshcart.common.exception.BusinessException;
import com.shaltout.freshcart.common.exception.ResourceNotFoundException;
import com.shaltout.freshcart.order.dto.OrderRequest;
import com.shaltout.freshcart.order.dto.OrderResponse;
import com.shaltout.freshcart.order.entity.Order;
import com.shaltout.freshcart.order.mapper.OrderMapper;
import com.shaltout.freshcart.order.repository.OrderRepository;
import com.shaltout.freshcart.payment.service.PaymentService;
import com.shaltout.freshcart.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final OrderMapper orderMapper;

    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public OrderResponse createOrder(OrderRequest request) {
        log.info("Creating order for user: {}", getCurrentUserId());

        // Get current user's cart
        CartResponse cartResponse = cartService.getCurrentUserCart();

        if (cartResponse.getItems().isEmpty()) {
            throw new BusinessException("Cart is empty");
        }

        // Generate order number
        String orderNumber = generateOrderNumber();

        // Create order from cart
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(getCurrentUserId())
                .status(Order.OrderStatus.PENDING)
                .shippingAddress(mapToShippingAddress(request.getShippingAddress()))
                .items(cartResponse.getItems().stream()
                        .map(this::mapToOrderItem)
                        .toList())
                .summary(mapToOrderSummary(cartResponse.getSummary()))
                .paymentInfo(Order.PaymentInfo.builder()
                        .method(request.getPaymentMethod())
                        .status(Order.PaymentStatus.PENDING)
                        .build())
                .build();

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Update product stock
        updateProductStock(savedOrder);

        // Convert cart to order
        cartService.convertCurrentUserCartToOrder(savedOrder.getId());

        // Process payment
        processPayment(savedOrder, request);

        return orderMapper.toResponse(savedOrder);
    }

    public OrderResponse getOrderById(String id) {
        log.info("Fetching order by id: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // Verify user owns this order
        if (!order.getUserId().equals(getCurrentUserId())) {
            throw new BusinessException("Access denied");
        }

        return orderMapper.toResponse(order);
    }

    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        log.info("Fetching order by order number: {}", orderNumber);
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUserId().equals(getCurrentUserId())) {
            throw new BusinessException("Access denied");
        }

        return orderMapper.toResponse(order);
    }

    public Page<OrderResponse> getUserOrders(Pageable pageable) {
        log.info("Fetching orders for user: {}", getCurrentUserId());
        return orderRepository.findByUserId(getCurrentUserId(), pageable)
                .map(orderMapper::toResponse);
    }

    public OrderResponse updateOrderStatus(String orderId, Order.OrderStatus status) {
        log.info("Updating order {} status to: {}", orderId, status);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toResponse(updatedOrder);
    }

    public OrderResponse cancelOrder(String orderId) {
        log.info("Cancelling order: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUserId().equals(getCurrentUserId())) {
            throw new BusinessException("Access denied");
        }

        if (order.getStatus() != Order.OrderStatus.PENDING &&
                order.getStatus() != Order.OrderStatus.PROCESSING) {
            throw new BusinessException("Order cannot be cancelled in current status");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);

        // Restore product stock
        restoreProductStock(order);

        return orderMapper.toResponse(updatedOrder);
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() +
                "-" + System.currentTimeMillis() % 10000;
    }

    private Order.ShippingAddress mapToShippingAddress(OrderRequest.ShippingAddressDto dto) {
        return Order.ShippingAddress.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .zipCode(dto.getZipCode())
                .build();
    }

    private Order.OrderItem mapToOrderItem(CartResponse.CartItemDto cartItem) {
        return Order.OrderItem.builder()
                .productId(cartItem.getProductId())
                .productTitle(cartItem.getProductTitle())
                .productImage(cartItem.getProductImage())
                .price(cartItem.getPrice())
                .quantity(cartItem.getQuantity())
                .totalPrice(cartItem.getTotalPrice())
                .build();
    }

    private Order.OrderSummary mapToOrderSummary(CartResponse.CartSummaryDto cartSummary) {
        return Order.OrderSummary.builder()
                .totalItems(cartSummary.getTotalItems())
                .subtotal(cartSummary.getTotalPrice())
                .discount(cartSummary.getDiscount())
                .shipping(cartSummary.getShipping())
                .tax(cartSummary.getTax())
                .total(cartSummary.getGrandTotal())
                .build();
    }

    private void updateProductStock(Order order) {
        order.getItems().forEach(item -> {
            productService.updateProductStock(item.getProductId(), item.getQuantity());
        });
    }

    private void restoreProductStock(Order order) {
        order.getItems().forEach(item -> {
            // Restore stock when order is cancelled
            // This would need additional logic in ProductService
            log.info("Restoring stock for product: {}", item.getProductId());
        });
    }

    private void processPayment(Order order, OrderRequest request) {
        try {
            // Process payment through payment service
            String transactionId = paymentService.processPayment(
                    order.getId(),
                    order.getSummary().getTotal(),
                    request.getPaymentMethod()
            );

            // Update order with payment info
            order.getPaymentInfo().setTransactionId(transactionId);
            order.getPaymentInfo().setStatus(Order.PaymentStatus.PAID);
            order.getPaymentInfo().setPaidAt(LocalDateTime.now());
            order.setStatus(Order.OrderStatus.PROCESSING);

            orderRepository.save(order);

        } catch (Exception e) {
            log.error("Payment processing failed for order: {}", order.getId(), e);
            order.getPaymentInfo().setStatus(Order.PaymentStatus.FAILED);
            order.setStatus(Order.OrderStatus.CANCELLED);
            orderRepository.save(order);
            throw new BusinessException("Payment processing failed: " + e.getMessage());
        }
    }
}
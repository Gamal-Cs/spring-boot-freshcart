package com.shaltout.freshcart.cart.service;

import com.shaltout.freshcart.cart.dto.AddToCartRequest;
import com.shaltout.freshcart.cart.dto.CartResponse;
import com.shaltout.freshcart.cart.dto.UpdateCartItemRequest;
import com.shaltout.freshcart.cart.entity.Cart;
import com.shaltout.freshcart.cart.mapper.CartMapper;
import com.shaltout.freshcart.cart.repository.CartRepository;
import com.shaltout.freshcart.common.exception.BusinessException;
import com.shaltout.freshcart.common.exception.ResourceNotFoundException;
import com.shaltout.freshcart.product.entity.Product;
import com.shaltout.freshcart.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;
    private final CartMapper cartMapper;

    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Cacheable(value = "carts", key = "#userId")
    public CartResponse getCartByUserId(String userId) {
        log.info("Fetching cart for user: {}", userId);

        Cart cart = cartRepository.findByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE)
                .orElseGet(() -> createEmptyCart(userId));

        recalculateCart(cart);
        cartRepository.save(cart);
        return cartMapper.toResponse(cart);
    }

    public CartResponse getCurrentUserCart() {
        return getCartByUserId(getCurrentUserId());
    }

    @CacheEvict(value = "carts", key = "#userId")
    public CartResponse addItemToCart(String userId, AddToCartRequest request) {
        log.info("Adding item to cart for user: {}, product: {}", userId, request.getProductId());

        Product product = productService.getProductEntityById(request.getProductId());
        validateProductAvailability(product, request.getQuantity());

        Cart cart = cartRepository.findByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE)
                .orElseGet(() -> createEmptyCart(userId));

        addOrUpdateCartItem(cart, product, request.getQuantity());
        recalculateCart(cart);

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toResponse(savedCart);
    }

    public CartResponse addItemToCurrentUserCart(AddToCartRequest request) {
        return addItemToCart(getCurrentUserId(), request);
    }

    @CacheEvict(value = "carts", key = "#userId")
    public CartResponse updateCartItem(String userId, String productId, UpdateCartItemRequest request) {
        log.info("Updating cart item for user: {}, product: {}", userId, productId);

        Cart cart = cartRepository.findByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active cart not found"));

        Optional<Cart.CartItem> itemOptional = findCartItem(cart, productId);

        if (request.getQuantity() <= 0) {
            removeCartItem(cart, productId);
        } else if (itemOptional.isPresent()) {
            Cart.CartItem item = itemOptional.get();
            Product product = productService.getProductEntityById(productId);
            validateProductAvailability(product, request.getQuantity());
            item.setQuantity(request.getQuantity());
            item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        } else {
            throw new ResourceNotFoundException("Product not found in cart");
        }

        recalculateCart(cart);
        Cart updatedCart = cartRepository.save(cart);
        return cartMapper.toResponse(updatedCart);
    }

    public CartResponse updateCurrentUserCartItem(String productId, UpdateCartItemRequest request) {
        return updateCartItem(getCurrentUserId(), productId, request);
    }

    @CacheEvict(value = "carts", key = "#userId")
    public CartResponse removeItemFromCart(String userId, String productId) {
        log.info("Removing item from cart for user: {}, product: {}", userId, productId);

        Cart cart = cartRepository.findByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active cart not found"));

        removeCartItem(cart, productId);
        recalculateCart(cart);

        Cart updatedCart = cartRepository.save(cart);
        return cartMapper.toResponse(updatedCart);
    }

    public CartResponse removeItemFromCurrentUserCart(String productId) {
        return removeItemFromCart(getCurrentUserId(), productId);
    }

    @CacheEvict(value = "carts", key = "#userId")
    public void clearCart(String userId) {
        log.info("Clearing cart for user: {}", userId);

        Cart cart = cartRepository.findByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active cart not found"));

        cart.getItems().clear();
        recalculateCart(cart);
        cartRepository.save(cart);
    }

    public void clearCurrentUserCart() {
        clearCart(getCurrentUserId());
    }

    public void convertCartToOrder(String userId, String orderId) {
        log.info("Converting cart to order for user: {}", userId);

        Cart cart = cartRepository.findByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active cart not found"));

        cart.setStatus(Cart.CartStatus.CONVERTED);
        cartRepository.save(cart);
    }

    public void convertCurrentUserCartToOrder(String orderId) {
        convertCartToOrder(getCurrentUserId(), orderId);
    }

    private Cart createEmptyCart(String userId) {
        return Cart.builder()
                .userId(userId)
                .status(Cart.CartStatus.ACTIVE)
                .items(new ArrayList<>())
                .summary(createEmptySummary())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
    }

    private Cart.CartSummary createEmptySummary() {
        return Cart.CartSummary.builder()
                .totalItems(0)
                .totalPrice(BigDecimal.ZERO)
                .discount(BigDecimal.ZERO)
                .shipping(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .grandTotal(BigDecimal.ZERO)
                .build();
    }

    private void addOrUpdateCartItem(Cart cart, Product product, Integer quantity) {
        Optional<Cart.CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            Cart.CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        } else {
            Cart.CartItem newItem = Cart.CartItem.builder()
                    .productId(product.getId())
                    .productTitle(product.getTitle())
                    .productImage(product.getImageCover())
                    .price(product.getPrice())
                    .quantity(quantity)
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
                    .build();
            cart.getItems().add(newItem);
        }
    }

    private Optional<Cart.CartItem> findCartItem(Cart cart, String productId) {
        return cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
    }

    private void removeCartItem(Cart cart, String productId) {
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
    }

    private void recalculateCart(Cart cart) {
        int totalItems = cart.getItems().stream()
                .mapToInt(Cart.CartItem::getQuantity)
                .sum();

        BigDecimal totalPrice = cart.getItems().stream()
                .map(Cart.CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shipping = calculateShipping(totalPrice);
        BigDecimal tax = calculateTax(totalPrice);
        BigDecimal discount = cart.getDiscount() != null ? cart.getDiscount() : BigDecimal.ZERO;
        BigDecimal grandTotal = totalPrice
                .subtract(discount)
                .add(shipping)
                .add(tax);

        cart.setSummary(Cart.CartSummary.builder()
                .totalItems(totalItems)
                .totalPrice(totalPrice)
                .discount(discount)
                .shipping(shipping)
                .tax(tax)
                .grandTotal(grandTotal)
                .build());
    }

    private BigDecimal calculateShipping(BigDecimal totalPrice) {
        // Free shipping for orders over $100
        if (totalPrice.compareTo(BigDecimal.valueOf(100)) >= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(10); // Flat rate shipping
    }

    private BigDecimal calculateTax(BigDecimal totalPrice) {
        // 10% tax
        return totalPrice.multiply(BigDecimal.valueOf(0.1));
    }

    private void validateProductAvailability(Product product, Integer requestedQuantity) {
        if (!product.getActive()) {
            throw new BusinessException("Product is not available");
        }

        if (product.getQuantity() < requestedQuantity) {
            throw new BusinessException("Insufficient stock. Available: " + product.getQuantity());
        }
    }

    @Scheduled(cron = "0 0 0 * * ?") // Run daily at midnight
    public void cleanupAbandonedCarts() {
        log.info("Cleaning up abandoned carts");
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);

        cartRepository.findAllByStatusAndUpdatedAtBefore(
                        Cart.CartStatus.ACTIVE, cutoff)
                .forEach(cart -> {
                    cart.setStatus(Cart.CartStatus.ABANDONED);
                    cartRepository.save(cart);
                    log.info("Marked cart {} as abandoned", cart.getId());
                });
    }
}
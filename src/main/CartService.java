package com.ecommerce.cart.service;

import com.ecommerce.cart.dto.AddToCartRequest;
import com.ecommerce.cart.dto.CartResponse;
import com.ecommerce.cart.dto.UpdateCartItemRequest;
import com.ecommerce.cart.entity.Cart;
import com.ecommerce.cart.entity.Cart.CartItem;
import com.ecommerce.cart.entity.Cart.CartSummary;
import com.ecommerce.cart.mapper.CartMapper;
import com.ecommerce.cart.repository.CartRepository;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final CartMapper cartMapper;
    
    @Cacheable(value = "carts", key = "#userId")
    public CartResponse getCartByUserId(String userId) {
        log.info("Fetching cart for user: {}", userId);
        
        Cart cart = cartRepository.findByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE)
                .orElseGet(() -> createEmptyCart(userId));
        
        recalculateCart(cart);
        return cartMapper.toResponse(cart);
    }
    
    @CacheEvict(value = "carts", key = "#userId")
    public CartResponse addItemToCart(String userId, AddToCartRequest request) {
        log.info("Adding item to cart for user: {}, product: {}", userId, request.getProductId());
        
        Product product = productService.getProductById(request.getProductId());
        validateProductAvailability(product, request.getQuantity());
        
        Cart cart = cartRepository.findByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE)
                .orElseGet(() -> createEmptyCart(userId));
        
        addOrUpdateCartItem(cart, product, request.getQuantity());
        recalculateCart(cart);
        
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toResponse(savedCart);
   
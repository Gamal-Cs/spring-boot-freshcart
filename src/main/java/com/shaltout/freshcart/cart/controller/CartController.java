package com.shaltout.freshcart.cart.controller;

import com.shaltout.freshcart.cart.dto.AddToCartRequest;
import com.shaltout.freshcart.cart.dto.CartResponse;
import com.shaltout.freshcart.cart.dto.UpdateCartItemRequest;
import com.shaltout.freshcart.cart.service.CartService;
import com.shaltout.freshcart.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
@Tag(name = "Carts", description = "Shopping cart management APIs")
public class CartController {

    private final CartService cartService;

    @GetMapping("/me")
    @Operation(summary = "Get current user's cart")
    public ApiResponse<CartResponse> getCurrentUserCart() {
        return ApiResponse.success(cartService.getCurrentUserCart());
    }

    @PostMapping("/me/items")
    @Operation(summary = "Add item to cart")
    public ApiResponse<CartResponse> addItemToCart(@Valid @RequestBody AddToCartRequest request) {
        return ApiResponse.success(
                cartService.addItemToCurrentUserCart(request),
                "Item added to cart successfully"
        );
    }

    @PutMapping("/me/items/{productId}")
    @Operation(summary = "Update cart item quantity")
    public ApiResponse<CartResponse> updateCartItem(
            @PathVariable String productId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ApiResponse.success(
                cartService.updateCurrentUserCartItem(productId, request),
                "Cart item updated successfully"
        );
    }

    @DeleteMapping("/me/items/{productId}")
    @Operation(summary = "Remove item from cart")
    public ApiResponse<CartResponse> removeItemFromCart(@PathVariable String productId) {
        return ApiResponse.success(
                cartService.removeItemFromCurrentUserCart(productId),
                "Item removed from cart successfully"
        );
    }

    @DeleteMapping("/me")
    @Operation(summary = "Clear cart")
    public ApiResponse<Void> clearCart() {
        cartService.clearCurrentUserCart();
        return ApiResponse.success(null, "Cart cleared successfully");
    }
}
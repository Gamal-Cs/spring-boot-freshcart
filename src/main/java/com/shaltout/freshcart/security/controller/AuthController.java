package com.shaltout.freshcart.security.controller;

import com.shaltout.freshcart.common.dto.ApiResponse;
import com.shaltout.freshcart.security.dto.AuthRequest;
import com.shaltout.freshcart.security.dto.AuthResponse;
import com.shaltout.freshcart.security.dto.RegisterRequest;
import com.shaltout.freshcart.security.dto.TokenRefreshRequest;
import com.shaltout.freshcart.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request), "Registration successful");
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ApiResponse.success(authService.login(request), "Login successful");
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ApiResponse<AuthResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return ApiResponse.success(authService.refreshToken(request), "Token refreshed successfully");
    }
}
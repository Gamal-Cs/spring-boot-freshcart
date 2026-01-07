package com.shaltout.freshcart.user.controller;

import com.shaltout.freshcart.common.dto.ApiResponse;
import com.shaltout.freshcart.user.dto.ProfileUpdateRequest;
import com.shaltout.freshcart.user.dto.UserDto;
import com.shaltout.freshcart.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ApiResponse<UserDto> getCurrentUser() {
        return ApiResponse.success(userService.getCurrentUser());
    }

    @PutMapping("/me")
    @Operation(summary = "Update user profile")
    public ApiResponse<UserDto> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return ApiResponse.success(userService.updateProfile(request), "Profile updated successfully");
    }

    @PatchMapping("/me/password")
    @Operation(summary = "Change password")
    public ApiResponse<Void> changePassword(@RequestBody Map<String, String> passwords) {
        String currentPassword = passwords.get("currentPassword");
        String newPassword = passwords.get("newPassword");

        userService.changePassword(currentPassword, newPassword);
        return ApiResponse.success(null, "Password changed successfully");
    }
}
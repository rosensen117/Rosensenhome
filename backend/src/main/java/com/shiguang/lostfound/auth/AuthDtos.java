package com.shiguang.lostfound.auth;

import jakarta.validation.constraints.*;

public final class AuthDtos {
    private AuthDtos() {}

    public record RegisterRequest(
            @NotBlank(message = "姓名不能为空") @Size(max = 40, message = "姓名最多40个字符") String name,
            @NotBlank(message = "学号不能为空") @Size(max = 32, message = "学号格式不正确") String studentId,
            @NotBlank(message = "手机号不能为空") @Pattern(regexp = "^1\\d{10}$", message = "请输入正确的11位手机号") String phone,
            @Email(message = "邮箱格式不正确") @Size(max = 120, message = "邮箱过长") String email,
            @NotBlank(message = "密码不能为空") @Size(min = 6, max = 72, message = "密码长度应为6到72位") String password
    ) {}

    public record LoginRequest(
            @NotBlank(message = "账号不能为空") String account,
            @NotBlank(message = "密码不能为空") String password
    ) {}

    public record UserResponse(Long id, String name, String studentId, String phone, String email, String role, boolean verified, String avatarUrl) {
        static UserResponse from(UserAccount user) {
            return new UserResponse(user.getId(), user.getName(), user.getStudentId(), user.getPhone(), user.getEmail(), user.getRole().name(), user.isVerified(), user.getAvatarUrl());
        }
    }

    public record AvatarRequest(
            @NotBlank(message = "头像对象标识不能为空") @Size(max = 300) String key,
            @NotBlank(message = "头像地址不能为空") @Size(max = 600) String url
    ) {}

    public record AuthResponse(String token, long expiresIn, UserResponse user) {}
    public record MessageResponse(String message) {}
}

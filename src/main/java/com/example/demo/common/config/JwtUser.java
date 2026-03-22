package com.example.demo.common.config;

public record JwtUser(
        Long userId,
        String email,
        String role
) {
}

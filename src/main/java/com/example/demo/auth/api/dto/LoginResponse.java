package com.example.demo.auth.api.dto;

public record LoginResponse(
        Long userId,
        String email,
        String role,
        String accessToken,
        String tokenType,
        long expiresInSeconds
) {
}

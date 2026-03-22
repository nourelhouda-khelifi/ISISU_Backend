package com.example.demo.auth.api.dto;

public record AuthMeResponse(
        Long userId,
        String email,
        String role
) {
}

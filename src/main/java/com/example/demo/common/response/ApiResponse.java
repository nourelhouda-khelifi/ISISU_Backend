package com.example.demo.common.response;

import java.time.Instant;

public record ApiResponse<T>(
        Instant timestamp,
        int status,
        boolean success,
        String message,
        T data
) {
    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return new ApiResponse<>(Instant.now(), status, true, message, data);
    }

    public static <T> ApiResponse<T> error(int status, String message, T data) {
        return new ApiResponse<>(Instant.now(), status, false, message, data);
    }
}

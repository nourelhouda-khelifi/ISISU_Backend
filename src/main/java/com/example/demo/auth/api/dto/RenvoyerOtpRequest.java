package com.example.demo.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RenvoyerOtpRequest(
        @NotBlank @Email String email
) {
}

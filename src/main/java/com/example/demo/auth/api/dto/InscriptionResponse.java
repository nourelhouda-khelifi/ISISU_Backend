package com.example.demo.auth.api.dto;

public record InscriptionResponse(
        Long userId,
        String email,
        String statut,
        String message
) {
}

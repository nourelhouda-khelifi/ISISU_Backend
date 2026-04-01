package com.example.demo.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO pour créer une nouvelle session
 */
@Data
@AllArgsConstructor
@Builder
public class CreateSessionRequest {
    // Pas de paramètre requis — le backend prend l'utilisateur du JWT
}

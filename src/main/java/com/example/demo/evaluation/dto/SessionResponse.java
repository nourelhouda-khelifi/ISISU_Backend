package com.example.demo.evaluation.dto;

import com.example.demo.evaluation.domain.enums.StatutSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO pour réponse de création de session
 */
@Data
@AllArgsConstructor
@Builder
public class SessionResponse {
    private Long sessionId;
    private StatutSession statut;
    private LocalDateTime dateDebut;
    private Integer tempsRestantSecondes;
    private Integer totalQuestions;
    private String message;
}

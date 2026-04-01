package com.example.demo.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO pour le statut actuel d'une session
 */
@Data
@AllArgsConstructor
@Builder
public class SessionStatusDTO {
    private Long sessionId;
    private String statut;
    private LocalDateTime dateDebut;
    private Integer tempsRestantSecondes;
    private Integer questionsRepondues;
    private Integer totalQuestions;
    private Double pourcentageAvancement;
    private String currentModuleCode;
    private String currentModuleNom;
}

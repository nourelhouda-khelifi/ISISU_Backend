package com.example.demo.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO pour l'historique des sessions de l'utilisateur
 */
@Data
@AllArgsConstructor
@Builder
public class SessionHistoryDTO {
    private Long sessionId;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String statut;
    private Integer dureeMinutes;
    private Double scoreGlobal;
    private Integer lacunesDetectees;
}

package com.example.demo.evaluation.dto;

import com.example.demo.evaluation.domain.enums.StatutCompetence;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour afficher les résultats finaux d'une session
 */
@Data
@AllArgsConstructor
@Builder
public class SessionResultsDTO {
    private Long sessionId;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Integer dureeMinutes;
    private Integer totalQuestionsRepondues;
    private Integer totalCorrect;
    private Double scoreGlobal;
    private List<CompetenceScoreDTO> competences;
}

package com.example.demo.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO pour les statistiques des questions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatistiquesQuestionsDTO {
    private Map<String, QuestionStatsDTO> parType;
    private Map<String, QuestionStatsDTO> parDifficulte;
    
    /**
     * DTO pour les stats d'une catégorie de question
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionStatsDTO {
        private Long nombre;
        private Double tauxReussite;
        private Double tauxMoyenReponses;
        private Long nombreUtilisations;
    }
}

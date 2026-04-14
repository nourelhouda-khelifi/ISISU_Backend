package com.example.demo.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO pour les statistiques des compétences
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatistiquesCompetencesDTO {
    private List<TopCompetenceDTO> topCompetences;
    private List<CompetenceTendanceDTO> competencesTendances;
    
    /**
     * DTO pour une compétence dans le top
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopCompetenceDTO {
        private Long id;
        private String nom;
        private Double scoreMoyen;
        private Long nombreApprenants;
        private Double tauxReussite;
    }
    
    /**
     * DTO pour les tendances des compétences
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetenceTendanceDTO {
        private Long id;
        private String nom;
        private Map<String, ScoreWeekDTO> semaines;
    }
    
    /**
     * DTO pour le score hebdomadaire
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoreWeekDTO {
        private Double score;
        private Long nombreTests;
    }
}

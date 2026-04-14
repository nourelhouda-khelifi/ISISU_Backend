package com.example.demo.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO pour les statistiques des performances
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatistiquesPerformancesDTO {
    private List<DistributionScoreDTO> distributionScore;
    private Map<String, PerformanceApprenantsDTO> parTypApprenant;
    
    /**
     * DTO pour la distribution des scores
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistributionScoreDTO {
        private String range;
        private Long nombre;
        private Double pourcentage;
    }
    
    /**
     * DTO pour les performances par type d'apprenant
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceApprenantsDTO {
        private Double scoreMoyen;
        private Long nombreApprenants;
        private Double tauxReussite;
    }
}

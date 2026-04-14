package com.example.demo.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO pour la heatmap des statistiques (compétence par difficulté)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatistiquesHeatmapDTO {
    private List<HeatmapItemDTO> competenceParDifficulte;
    
    /**
     * DTO pour chaque compétence dans la heatmap
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HeatmapItemDTO {
        private String competence;
        private Long competenceId;
        private Map<String, Double> performanceParDifficulte;
    }
}

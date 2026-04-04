package com.example.demo.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour recommandation cross-session (agrège TOUTES les sessions)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrossSessionRecommendationDTO {
    
    private StudentInfoDTO studentProfile;
    private List<String> keyInsights;
    private List<CompetenceTimelineDTO> competenceEvolution;
    private List<PrioritizedRecommendationDTO> recommendations;
    private PathForwardDTO pathForward;
    private OverallMetricsDTO metrics;
}

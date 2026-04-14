package com.example.demo.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrioritizedRecommendationDTO {
    private String priority; // P1-CRITICAL, P2-HIGH, P3-MEDIUM, P4-OPTIONAL
    private String competence;
    private String moduleName;
    private Long moduleId;
    private String moduleCode;
    private String status; // MOMENTUM, STAGNATION, etc.
    private Double currentScore;
    private String trend;
    private String timelineReason;
    private List<String> actions;
    private String nextSteps;
    private Integer sessionsToMastery; // Estimated remaining sessions
    private List<String> blockingOtherModules;
}

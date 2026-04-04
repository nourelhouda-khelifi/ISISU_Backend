package com.example.demo.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OverallMetricsDTO {
    private Double globalScore;
    private String globalTrend;
    private Double averageVelocity;
    private Integer totalQuestionsSolved;
    private Integer totalCorrectAnswers;
    private Double accuracyRate;
    private Integer totalSessionsDuration; // minutes
    private Integer estimatedTimeToCompletion; // hours
}

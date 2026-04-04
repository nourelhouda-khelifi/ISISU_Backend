package com.example.demo.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Progression globale de l'apprenant
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressionDTO {
    
    private Integer completionPercent;
    private Long sessionsTerminees;
    private Long questionsRepondues;
}

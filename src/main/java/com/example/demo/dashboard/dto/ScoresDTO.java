package com.example.demo.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Résumé des scores de l'apprenant
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoresDTO {
    
    private Double scoreMoyenGlobal;
    private Double scoreDerniereSession;
    private String evolutionScore;
}

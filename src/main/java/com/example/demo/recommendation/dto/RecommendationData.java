package com.example.demo.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.Map;

/**
 * Données complètes structurées pour les recommandations (PHASE 1: ALGO MANUEL)
 * 
 * Ceci est le résultat du traitement algorithmique pur (aucun LLM).
 * C'est ce qu'on envorera au LLM pour enrichissement à la PHASE 2.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationData {
    
    /**
     * Profil de l'étudiant
     */
    private StudentProfile studentProfile;
    
    /**
     * Scores par module
     */
    private List<ModuleScore> scoresByModule;
    
    /**
     * Progression sur 3 dernières sessions
     */
    private Progression progression;
    
    /**
     * Map: Module → Modules qu'il bloque
     */
    private Map<String, BlockingDependency> blockingDependencies;
    
    /**
     * Modules où l'étudiant excelle (score > 85%)
     */
    private List<StrengthPoint> strengths;
    
    /**
     * Lacunes critiques à adresser (score < 50%)
     */
    private List<CriticalGap> criticalGaps;
}

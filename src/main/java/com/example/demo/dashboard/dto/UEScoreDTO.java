package com.example.demo.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO pour afficher le score agrégé d'une Unité d'Enseignement (UE)
 * 
 * Exemple: "Ingénieurie de données" (UE) contient:
 *   - BDD (Module) → SQL, Modélisation → Compétences
 *   - Génie Logiciel (Module) → Patterns, Architecture → Compétences
 * 
 * scoreGlobal = moyenne des scores de TOUTES les compétences de l'UE
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UEScoreDTO {
    
    /**
     * ID de l'UE
     */
    private Long ueId;
    
    /**
     * Code officiel (ex: "E3-1-ID")
     */
    private String codeUE;
    
    /**
     * Libellé complet (ex: "Ingénieurie de données")
     */
    private String libelle;
    
    /**
     * Score moyen de l'UE (0.0 à 100.0)
     * = moyenne de tous les scores des compétences de cette UE
     */
    private Double scoreMoyenUE;
    
    /**
     * Nombre de compétences évaluées pour cette UE
     */
    private Integer nbCompetencesEvaluees;
    
    /**
     * Nombre de compétences acquises (statut ACQUIS ou MAITRISE)
     */
    private Integer nbCompetencesAcquises;
    
    /**
     * Pourcentage de compétences acquises
     * = (nbCompetencesAcquises / nbCompetencesEvaluees) * 100
     */
    private Double tauxAcquisition;
    
    /**
     * Scores détaillés par module de l'UE
     */
    @Builder.Default
    private List<ModuleScoreDTO> moduleScores = new ArrayList<>();
    
    /**
     * Statut global de l'UE
     * LACUNE: < 40%, A_RENFORCER: 40-60%, BON: > 60%
     */
    private String statut;
    
    /**
     * DTO imbriqué pour le score par module
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ModuleScoreDTO {
        private Long moduleId;
        private String codeModule;
        private String nomModule;
        private Double scoreMoyenModule;
        private Integer nbCompetences;
        private String statut;
    }
}

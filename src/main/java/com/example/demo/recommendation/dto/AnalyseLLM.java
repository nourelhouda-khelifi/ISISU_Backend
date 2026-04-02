package com.example.demo.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO représentant l'analyse enrichie par le LLM (Gemini)
 * Résultat de la PHASE 2 du système de recommendations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyseLLM {

    /**
     * Message de bienvenue personnalisé pour l'étudiant (2-3 phrases)
     */
    private String messagePersonnalise;

    /**
     * Analyse globale du profil de l'étudiant (3-4 phrases)
     */
    private String analysePrincipale;

    /**
     * Top 3 priorités d'amélioration
     */
    private List<PrioriteDTO> priorites;

    /**
     * Parcours FIE4 recommandé (ex: "Développement", "IA & Big Data", "Management")
     * ou null si l'étudiant est en VAE
     */
    private String parcourRecommande;

    /**
     * Raison du choix du parcours recommandé
     */
    private String raisonParcours;

    /**
     * Message de motivation personnalisé pour l'étudiant
     */
    private String messageMotivation;

    /**
     * DTO représentant une priorité d'amélioration
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PrioriteDTO {

        /**
         * Nom du module problématique
         */
        private String module;

        /**
         * Niveau d'urgence: CRITIQUE | HAUTE | MOYENNE
         */
        private String urgence;

        /**
         * Raison pour laquelle ce module est prioritaire
         */
        private String raison;

        /**
         * Conseil concret et actionnable pour le module
         */
        private String conseil;
    }

    /**
     * Wrapper contenant PHASE 1 (données structurées) + PHASE 2 (analyse LLM)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnalyseLLMWithData {

        /**
         * PHASE 1: Données structurées calculées par l'algo manuel
         */
        private RecommendationData phaseStructuree;

        /**
         * PHASE 2: Analyse enrichie par le LLM
         */
        private AnalyseLLM analyseLLM;
    }
}

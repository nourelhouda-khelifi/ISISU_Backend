package com.example.demo.evaluation.domain.enums;

/**
 * Niveau atteint par l'étudiant lors de l'évaluation
 * 
 * Utilisé pour tracer le chemin parcouru et déterminer le statut
 */
public enum NiveauAtteint {
    /**
     * Pas de question posée - évaluation pas encore commencée
     */
    NON_DEMARRE,
    
    /**
     * Questions FACILE posées
     */
    FACILE,
    
    /**
     * Questions MOYEN posées
     */
    MOYEN,
    
    /**
     * Questions DIFFICILE posées
     */
    DIFFICILE
}

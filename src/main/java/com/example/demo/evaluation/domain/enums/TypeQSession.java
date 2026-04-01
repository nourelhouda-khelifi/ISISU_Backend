package com.example.demo.evaluation.domain.enums;

/**
 * Type de question dans une session de test
 */
public enum TypeQSession {
    /**
     * NORMALE: Question posée dans le flux normal
     */
    NORMALE,
    
    /**
     * CONFIRMATION: Deuxième question FACILE posée après un premier échec
     * Permet de confirmer une lacune
     */
    CONFIRMATION
}

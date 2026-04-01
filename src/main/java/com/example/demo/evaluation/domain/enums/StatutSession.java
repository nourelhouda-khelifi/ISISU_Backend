package com.example.demo.evaluation.domain.enums;

/**
 * Statut d'une session de test
 */
public enum StatutSession {
    /**
     * Session en cours — l'étudiant est en train de répondre
     */
    EN_COURS,
    
    /**
     * Session terminée — l'étudiant a cliqué terminer ou timer a expiré
     */
    TERMINEE,
    
    /**
     * Session abandonnée — l'étudiant a fermé ou quitté
     */
    ABANDONNEE,
    
    /**
     * Session timeout — délai de 2h dépassé
     */
    TIMEOUT
}

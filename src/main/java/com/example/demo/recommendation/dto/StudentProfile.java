package com.example.demo.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Profil étudiant pour les recommandations
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfile {
    
    /**
     * Niveau: FIE3, FIE4, CANDIDAT_VAE, ADMIN
     */
    private String niveau;
    
    /**
     * Parcours d'origine: BUT Informatique, Master, etc
     */
    private String parcours;
    
    /**
     * Nombre total de sessions efectuées
     */
    private int nbSessions;
}

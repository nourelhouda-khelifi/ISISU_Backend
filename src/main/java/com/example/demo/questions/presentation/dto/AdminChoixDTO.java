package com.example.demo.questions.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les Choix (Réponses) visible aux ADMINS uniquement
 * 
 * Cette version INCLUT estCorrect (contrairement à ChoixDTO)
 * Utilisé UNIQUEMENT par AdminQuestionController pour retourner les réponses correctes
 * 
 * Security: Ne jamais retourner cet objet aux utilisateurs normaux
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminChoixDTO {
    private Long id;
    private String contenu;
    private Integer ordre;
    private boolean estCorrect;  // ✅ INCLUS pour admin (caché pour les utilisateurs normaux)
}

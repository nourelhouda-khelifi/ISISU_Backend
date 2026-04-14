package com.example.demo.questions.presentation.dto;

import com.example.demo.questions.domain.enums.NiveauDifficulte;
import com.example.demo.questions.domain.enums.TypeQuestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour Questions visible aux ADMINS uniquement
 * 
 * Cette version INCLUT estCorrect dans les choix
 * Utilisé UNIQUEMENT par AdminQuestionController GET endpoints
 * 
 * Security: Ne jamais retourner cet objet aux utilisateurs normaux
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminQuestionDTO {
    private Long id;
    private String enonce;
    private TypeQuestion type;
    private NiveauDifficulte difficulte;
    private double ponderation;
    private Integer dureeSecondes;
    private boolean actif;
    private LocalDateTime dateCreation;
    private List<Long> competenceIds;
    private List<AdminChoixDTO> choix;  // ✅ Utilise AdminChoixDTO (avec estCorrect)
}

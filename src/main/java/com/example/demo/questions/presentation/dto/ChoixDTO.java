package com.example.demo.questions.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour un Choix de réponse
 * 
 * Note importante :
 * - estCorrect n'est JAMAIS retourné au client (brouillon)
 * - ordre est retourné pour aider l'affichage
 * - La correction se fait côté serveur uniquement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChoixDTO {
    private Long id;
    private String contenu;
    private Integer ordre;
    // estCorrect intentionnellement absent — résultats pas révélés côté client
}

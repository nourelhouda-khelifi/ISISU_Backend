package com.example.demo.questions.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour créer un Choix de réponse (admin)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateChoixDTO {
    private String contenu;
    private boolean estCorrect;
    private Integer ordre;
}

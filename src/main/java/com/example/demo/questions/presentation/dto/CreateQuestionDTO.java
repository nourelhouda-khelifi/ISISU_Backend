package com.example.demo.questions.presentation.dto;

import com.example.demo.questions.domain.enums.NiveauDifficulte;
import com.example.demo.questions.domain.enums.TypeQuestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO pour créer une Question (admin uniquement)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateQuestionDTO {
    private String enonce;
    private TypeQuestion type;
    private NiveauDifficulte difficulte;
    private Integer dureeSecondes;
    private List<Long> competenceIds;
    private List<CreateChoixDTO> choix;
}

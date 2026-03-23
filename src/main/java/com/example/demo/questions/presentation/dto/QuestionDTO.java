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
 * DTO de réponse pour une Question
 * (liste les questions avec leurs propriétés)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDTO {
    private Long id;
    private String enonce;
    private TypeQuestion type;
    private NiveauDifficulte difficulte;
    private double ponderation;  // 1.0, 1.5 ou 2.0
    private Integer dureeSecondes;
    private boolean actif;
    private LocalDateTime dateCreation;
    private List<Long> competenceIds;
    private List<ChoixDTO> choix;
}

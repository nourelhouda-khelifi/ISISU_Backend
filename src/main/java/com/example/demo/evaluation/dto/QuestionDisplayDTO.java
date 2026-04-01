package com.example.demo.evaluation.dto;

import com.example.demo.questions.domain.enums.TypeQuestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DTO pour représenter une question à afficher
 */
@Data
@AllArgsConstructor
@Builder
public class QuestionDisplayDTO {
    private Long questionSessionId;
    private Long questionId;
    private String enonce;
    private TypeQuestion type;
    private String niveauDifficulte;
    private Integer dureeRecommandeeSecondes;
    private List<ChoixDTO> choix;
    private String ordre;
    private String statut;
}

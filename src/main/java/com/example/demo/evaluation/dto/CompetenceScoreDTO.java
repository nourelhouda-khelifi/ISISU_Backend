package com.example.demo.evaluation.dto;

import com.example.demo.evaluation.domain.enums.StatutCompetence;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO pour le score d'une compétence
 */
@Data
@AllArgsConstructor
@Builder
public class CompetenceScoreDTO {
    private Long competenceId;
    private String intitule;
    private String codeModule;
    private StatutCompetence statut;
    private Double scoreObtenu;
    private String niveauAtteint;
    private Boolean confirmationLacune;
    private Double scoreSession_precedente;
    private String evolution;
}

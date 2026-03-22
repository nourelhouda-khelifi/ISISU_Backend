package com.example.demo.referentiel.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetenceDTO {
    
    private Long id;
    private String intitule;
    private String description;
    private Integer numeroOrdre;
    private Integer niveauAttendu;
    private Double poids;
}

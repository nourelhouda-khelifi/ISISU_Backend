package com.example.demo.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Score d'une compétence dans une session
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompetenceScoreDetailDTO {
    
    private String nom;
    private Double scoreAvant;
    private Double scoreApres;
    private String evolution;
}

package com.example.demo.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Statistiques d'une compétence
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompetenceStatsDTO {
    
    private Long id;
    private String nom;
    private Double scoreMoyen;
    private Long nombreApprenants;
    private Double tauxAcquisition;
    private String evolution;
}

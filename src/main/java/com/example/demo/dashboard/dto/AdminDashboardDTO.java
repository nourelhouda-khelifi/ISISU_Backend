package com.example.demo.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Vue d'ensemble du dashboard administrateur
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDTO {
    
    private Long totalUtilisateurs;
    private Long totalEtudiantsFIE3;
    private Long totalCandidatsVAE;
    private Double scoreMoyenGlobal;
    private Long sessionsEnCours;
    private Double tauxReussite;
    private List<CompetenceStatsDTO> competencesTopPerformance;
    private List<CompetenceStatsDTO> competencesLacunes;
}

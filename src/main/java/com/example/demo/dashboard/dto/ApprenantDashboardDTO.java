package com.example.demo.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Vue d'ensemble du dashboard apprenant
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprenantDashboardDTO {
    
    private UserInfoDTO utilisateur;
    private ProgressionDTO progression;
    private ScoresDTO scores;
    private List<CompetenceProgressDTO> competences;
    private List<ActiviteDTO> activiteRecente;
}

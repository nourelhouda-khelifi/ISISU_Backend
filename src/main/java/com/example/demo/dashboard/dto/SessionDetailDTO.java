package com.example.demo.dashboard.dto;

import com.example.demo.evaluation.domain.enums.StatutSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Détail d'une session pour l'apprenant
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDetailDTO {
    
    private Long sessionId;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private StatutSession statut;
    private Double scoreGlobal;
    private List<CompetenceScoreDetailDTO> competences;
}

package com.example.demo.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO pour l'historique complet d'un étudiant (vue admin)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminHistoriqueEtudiantDTO {
    
    private Long utilisateurId;
    private String email;
    private String nom;
    private String prenom;
    private Long totalSessions;
    private Long sessionsTerminees;
    private Double scoreGlobalMoyen;
    private List<SessionHistoriqueDTO> sessions;
    
    /**
     * Détail d'une session
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionHistoriqueDTO {
        private Long sessionId;
        private Integer numeroSession;
        private LocalDateTime dateDebut;
        private LocalDateTime dateFin;
        private String statut;
        private Integer dureeMinutes;
        private Double scoreGlobal;
        private Integer totalQuestions;
        private Integer questionsRepondues;
        private List<CompetenceScoreDTO> scoresCompetences;
        private String raison; // pour sessions abandonnées
    }
    
    /**
     * Score d'une compétence dans une session
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetenceScoreDTO {
        private Long competenceId;
        private String nom;
        private Double scoreObtenu;
        private String niveauAtteint;
        private String statut;
        private Integer nbBonnesReponses;
        private Integer nbQuestions;
    }
}

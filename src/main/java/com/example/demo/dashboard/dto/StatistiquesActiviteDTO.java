package com.example.demo.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO pour les statistiques d'activité
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatistiquesActiviteDTO {
    private List<SessionParJourDTO> sessionsParJour;
    private List<ApprenantsActifsDTO> apprenantsActifs;
    private Long totalSessions;
    private Long totalApprenantsActifs;
    
    /**
     * DTO pour les sessions par jour
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionParJourDTO {
        private LocalDate date;
        private Long nombre;
        private Double dureeParMoyenne;
        private Double completionRate;
    }
    
    /**
     * DTO pour les apprenants actifs
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApprenantsActifsDTO {
        private LocalDate date;
        private Long nombre;
    }
}

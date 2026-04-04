package com.example.demo.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Statistiques globales des sessions (admin)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionStatisticsDTO {
    
    private Long totalSessions;
    private Long sessionsTerminees;
    private Long sessionsAbandonnes;
    private Integer dureeParMoyenne;
    private Double scoreParMoyenne;
    private List<DailySessionCountDTO> sessionsParJour;
}

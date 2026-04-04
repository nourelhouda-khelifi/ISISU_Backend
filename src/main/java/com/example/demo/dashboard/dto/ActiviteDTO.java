package com.example.demo.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Activité récente de l'apprenant
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiviteDTO {
    
    private LocalDateTime date;
    private String type;  // SESSION, QUESTION, SCORE_UPDATED
    private String titre;
    private String resultat;
}

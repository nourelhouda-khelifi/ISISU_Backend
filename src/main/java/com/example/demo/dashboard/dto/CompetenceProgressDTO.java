package com.example.demo.dashboard.dto;

import com.example.demo.evaluation.domain.enums.NiveauAtteint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Progression de l'apprenant dans une compétence
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompetenceProgressDTO {
    
    private Long id;
    private String nom;
    private NiveauAtteint niveau;
    private Double score;
    private String lacunes;
}

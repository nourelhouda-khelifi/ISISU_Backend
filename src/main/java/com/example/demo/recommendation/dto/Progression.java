package com.example.demo.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/**
 * Analyse de la progression de l'étudiant
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Progression {
    
    private List<SessionProgressionPoint> sessions;
    private String tendance;  // PROGRESSION_POSITIVE, REGRESSION, STABLE
    private String velocite;  // ex: "+9.5% par session"
}

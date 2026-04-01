package com.example.demo.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Lacune critique à adresser en priorité
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriticalGap {
    
    private String module;
    private Double score;
    private String raison;  // Pourquoi c'est critique
}

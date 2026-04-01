package com.example.demo.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/**
 * Représente une dépendance bloquante
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockingDependency {
    
    private List<String> bloque;  // List of modules that are blocked
    private String severite;      // CRITIQUE, HAUTE, MOYEN
}

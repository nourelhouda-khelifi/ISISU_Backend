package com.example.demo.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Score pour un module
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleScore {
    
    private String module;
    private int score;
    private String status;  // LACUNE, A_RENFORCER, ACQUIS, MAITRISE
}

package com.example.demo.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Point de force (domaine où l'étudiant excelle)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StrengthPoint {
    
    private String module;
    private Double score;
}

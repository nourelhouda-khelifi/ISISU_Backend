package com.example.demo.referentiel.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simplifié d'un Module pour les listes imbriquées
 * Évite les références circulaires et réduit la charge JSON
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleFIESimpleDTO {
    
    private Long id;
    private String code;
    private String nom;
}

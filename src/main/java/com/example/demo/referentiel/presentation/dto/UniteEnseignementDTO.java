package com.example.demo.referentiel.presentation.dto;

import com.example.demo.referentiel.domain.enums.Semestre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniteEnseignementDTO {
    
    private Long id;
    private String code;
    private String libelle;
    private Integer ects;
    private Semestre semestre;
}

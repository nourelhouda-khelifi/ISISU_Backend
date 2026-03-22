package com.example.demo.referentiel.presentation.dto;

import com.example.demo.referentiel.domain.enums.Semestre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleFIEDTO {
    
    private Long id;
    private String code;
    private String nom;
    private Semestre semestre;
    private UniteEnseignementDTO uniteEnseignement;
    private Integer heuresCM;
    private Integer heuresTD;
    private Integer heuresTP;
    private Integer heuresProjet;
    private Integer heuresTotal;
    private boolean evaluable;
    private String prerequisTexte;
    
    @Builder.Default
    private List<CompetenceDTO> competences = new ArrayList<>();
    
    @Builder.Default
    private List<ModuleFIESimpleDTO> modulesPrerequisList = new ArrayList<>();
}

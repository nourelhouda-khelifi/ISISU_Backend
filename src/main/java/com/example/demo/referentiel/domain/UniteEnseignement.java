package com.example.demo.referentiel.domain;

import com.example.demo.referentiel.domain.enums.Semestre;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Unité d'Enseignement (UE) — table légère et stable.
 *
 * Données initiales (9 lignes fixes insérées par Flyway) :
 *   S5 : E3-1-IN, E3-1-ID, E3-1-IS, E3-1-DI
 *   S6 : E3-2-IN, E3-2-IS, E3-2-DI, E3-2-PROJET, E3-2-STAGE
 */
@Entity
@Table(name = "unites_enseignement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniteEnseignement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Code officiel ISIS (unique)
     * Exemple : "E3-1-IN", "E3-2-ID"
     */
    @Column(nullable = false, unique = true, length = 20)
    private String code;

    /**
     * Libellé complet
     * Exemple : "Ingénierie numérique"
     */
    @Column(nullable = false, length = 100)
    private String libelle;

    /**
     * Crédits ECTS de l'UE
     */
    @Column(nullable = false)
    private Integer ects;

    /**
     * Semestre de l'UE (S5 ou S6)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Semestre semestre;

    /**
     * Modules rattachés à cette UE
     */
    @OneToMany(
        mappedBy = "uniteEnseignement",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<ModuleFIE> modules = new ArrayList<>();
}

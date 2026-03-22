package com.example.demo.referentiel.domain;

import com.example.demo.referentiel.domain.enums.Semestre;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Module d'enseignement FIE3 — extrait du syllabus officiel ISIS.
 * Nommé ModuleFIE pour éviter le conflit avec java.lang.Module.
 *
 * 11 modules ÉVALUABLES par ISIS-U (evaluable = true) :
 *   E3-1-IN-1 : Programmation orientée objet    (S5)
 *   E3-1-IN-2 : Génie Logiciel                  (S5)
 *   E3-1-IN-3 : Gestion de projet               (S5)
 *   E3-1-ID-1 : Systèmes d'information          (S5)
 *   E3-1-ID-2 : Base de données                 (S5)
 *   E3-1-ID-3 : Épidémiologie & santé données   (S5)
 *   E3-1-IS-2 : Fondamentaux IA                 (S5)
 *   E3-2-IN-1 : Technologies Web                (S6)
 *   E3-2-IN-2 : DevOps et réseaux               (S6)
 *   E3-2-IN-3 : Conception centrée utilisateur  (S6)
 *   E3-2-IS-4 : Imagerie médicale               (S6)
 */
@Entity
@Table(name = "modules_fie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleFIE {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Code officiel ISIS (unique)
     * Exemple : "E3-1-IN-1", "E3-2-IN-1"
     */
    @Column(nullable = false, unique = true, length = 20)
    private String code;

    /**
     * Nom complet du module
     * Exemple : "Programmation orientée objet"
     */
    @Column(nullable = false, length = 150)
    private String nom;

    /**
     * Semestre du module (S5 ou S6)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Semestre semestre;

    /**
     * UE à laquelle appartient ce module
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ue_id", nullable = false)
    private UniteEnseignement uniteEnseignement;

    /**
     * Volumes horaires extraits du syllabus
     */
    @Column(nullable = false)
    private Integer heuresCM;

    @Column(nullable = false)
    private Integer heuresTD;

    @Column(nullable = false)
    private Integer heuresTP;

    @Column(nullable = false)
    private Integer heuresProjet;

    @Column(nullable = false)
    private Integer heuresTotal;

    /**
     * true  → module évalué par ISIS-U
     * false → Sport, Langues, RSE, Stage...
     */
    @Column(nullable = false)
    private boolean evaluable;

    /**
     * Texte des prérequis extrait du syllabus
     * Exemple : "Bases algorithmique et programmation Java"
     */
    @Column(columnDefinition = "TEXT")
    private String prerequisTexte;

    /**
     * Modules prérequis de celui-ci (graphe d'ordre d'étude)
     * Exemple : POO Java → prérequis de → Technologies Web
     */
    @ManyToMany
    @JoinTable(
        name = "modules_prerequis",
        joinColumns = @JoinColumn(name = "module_id"),
        inverseJoinColumns = @JoinColumn(name = "prerequis_id")
    )
    @Builder.Default
    private List<ModuleFIE> modulesPrerequisList = new ArrayList<>();

    /**
     * Compétences visées par ce module
     * Chaque compétence est liée aux questions, scores et recommandations
     */
    @OneToMany(
        mappedBy = "module",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    @Builder.Default
    private List<Competence> competences = new ArrayList<>();
}

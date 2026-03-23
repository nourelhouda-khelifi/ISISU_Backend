package com.example.demo.referentiel.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Compétence visée par un module — cœur pédagogique d'ISIS-U.
 *
 * C'est l'élément central autour duquel tout gravite :
 *   - Les QUESTIONS évaluent une compétence précise
 *   - Les SCORES sont calculés par compétence
 *   - Les RECOMMANDATIONS sont générées par compétence
 *   - Le DASHBOARD affiche la progression par compétence
 *
 * 42 compétences extraites du syllabus FIE3 :
 *   Réparties entre les 11 modules évaluables
 *   Avec poids, niveauAttendu, et graphe de prérequis
 */
@Entity
@Table(name = "competences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Competence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Intitulé court de la compétence
     * Exemple : "Écrire des requêtes SQL complexes"
     */
    @Column(nullable = false, length = 255)
    private String intitule;

    /**
     * Description détaillée (ce qu'on évalue concrètement)
     * Exemple : "SELECT avec JOIN, GROUP BY, HAVING, sous-requêtes"
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Numéro d'ordre dans le syllabus (1 à 42)
     * Utilisé pour afficher les compétences dans l'ordre du syllabus
     */
    @Column(name = "numero_ordre", nullable = false)
    private Integer numeroOrdre;

    /**
     * Niveau de difficulté attendu
     * 1 = basique  → questions FACILE
     * 2 = moyen    → questions MOYEN
     * 3 = avancé   → questions DIFFICILE
     */
    @Column(name = "niveau_attendu", nullable = false)
    private Integer niveauAttendu;

    /**
     * Poids de cette compétence dans le score global du module
     * La somme des poids de toutes les compétences d'un module = 1.0
     * Exemple BDD : SQL (0.4) + Modélisation (0.3) + Triggers (0.3) = 1.0
     */
    @Column(nullable = false)
    private Double poids;

    /**
     * Module auquel appartient cette compétence
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private ModuleFIE module;

    /**
     * Compétences prérequis de celle-ci
     * Exemple : "Créer des procédures stockées"
     *           nécessite "Écrire des requêtes SQL"
     *
     * Utilisé par l'algorithme de recommandation pour
     * prioriser les lacunes fondamentales avant les avancées.
     */
    @ManyToMany
    @JoinTable(
        name = "competences_prerequis",
        joinColumns = @JoinColumn(name = "competence_id"),
        inverseJoinColumns = @JoinColumn(name = "prerequis_id")
    )
    @Builder.Default
    private List<Competence> prerequis = new ArrayList<>();
}

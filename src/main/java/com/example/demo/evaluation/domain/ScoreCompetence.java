package com.example.demo.evaluation.domain;

import com.example.demo.evaluation.domain.enums.NiveauAtteint;
import com.example.demo.evaluation.domain.enums.StatutCompetence;
import com.example.demo.referentiel.domain.Competence;
import jakarta.persistence.*;
import lombok.*;

/**
 * ScoreCompetence — Score final pour une compétence après évaluation
 * 
 * Contient:
 * - Le score obtenu (0% à 100%)
 * - Le statut (LACUNE, A_RENFORCER, ACQUIS, MAITRISE)
 * - Le niveau atteint (FACILE, MOYEN, DIFFICILE)
 * - L'évolution par rapport à la session précédente
 */
@Entity
@Table(name = "score_competence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreCompetence {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Session associée
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private SessionTest session;
    
    /**
     * Compétence évaluée
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competence_id", nullable = false)
    private Competence competence;
    
    /**
     * Score obtenu (0.0 à 1.0)
     * Formule: Σ(correct × pondération) / Σ(pondérations totales)
     * 
     * Exemple:
     * - FACILE ✅ (1.0) + MOYEN ✅ (1.5) + DIFFICILE ❌ (0.0)
     * = 2.5 / 4.5 = 0.56 = 56%
     */
    @Column(nullable = false, name = "score_obtenu")
    private Double scoreObtenu;
    
    /**
     * Statut final: LACUNE, A_RENFORCER, ACQUIS, MAITRISE
     * Basé sur le niveau atteint, pas juste sur le score %
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCompetence statut;
    
    /**
     * Niveau max atteint: NON_DEMARRE, FACILE, MOYEN, DIFFICILE
     * Utilisé pour déterminer le statut
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "niveau_atteint")
    private NiveauAtteint niveauAtteint = NiveauAtteint.NON_DEMARRE;
    
    /**
     * Nombre total de questions posées pour cette compétence
     */
    @Column(nullable = false, name = "nb_questions")
    private Integer nbQuestions = 0;
    
    /**
     * Nombre de bonnes réponses
     */
    @Column(nullable = false, name = "nb_bonnes_reponses")
    private Integer nbBonnesReponses = 0;
    
    /**
     * Score précédent (si session antérieure) — pour calculer évolution
     */
    @Column(name = "score_precedent")
    private Double scorePrecedent;
    
    /**
     * Évolution: score_actuel - score_precedent (peut être négatif)
     * Null si 1ère session
     */
    @Column(name = "evolution_pourcentage")
    private Double evolutionPourcentage;
    
    /**
     * Calculer l'évolution vs session précédente
     */
    public void calculerEvolution() {
        if (scorePrecedent != null) {
            evolutionPourcentage = (scoreObtenu - scorePrecedent) * 100;
        }
    }
}

package com.example.demo.questions.domain;

import com.example.demo.questions.domain.enums.NiveauDifficulte;
import com.example.demo.questions.domain.enums.TypeQuestion;
import com.example.demo.referentiel.domain.Competence;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Question de la banque ISIS-U.
 *
 * Une question :
 *   - appartient à un type précis (QCM, VRAI_FAUX, ORDRE...)
 *   - a un niveau de difficulté (FACILE, MOYEN, DIFFICILE)
 *   - est liée à UNE OU PLUSIEURS compétences du référentiel
 *   - contient ses choix de réponse (table Choix)
 *   - peut être activée/désactivée par l'admin (actif)
 *
 * Exemples :
 *
 *   QCM_SIMPLE / MOYEN / Compétence "SQL complexes" :
 *     "Quelle clause filtre après GROUP BY ?"
 *     Choix : WHERE(❌) HAVING(✅) FILTER(❌) ORDER BY(❌)
 *
 *   VRAI_FAUX / FACILE / Compétence "Modèle relationnel" :
 *     "Une clé étrangère peut être NULL"
 *     Choix : Vrai(✅) Faux(❌)
 *
 *   ORDRE / DIFFICILE / Compétence "Modèle OSI" :
 *     "Remettez les couches OSI de bas en haut"
 *     Choix : Physique(1) Liaison(2) Réseau(3) Transport(4)
 */
@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Énoncé de la question
     * Exemple : "Quelle clause SQL filtre après GROUP BY ?"
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String enonce;

    /**
     * Type de question — détermine la structure des choix
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeQuestion type;

    /**
     * Niveau de difficulté — impacte la pondération du score
     * FACILE=1.0 / MOYEN=1.5 / DIFFICILE=2.0
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulte", nullable = false)
    private NiveauDifficulte difficulte;

    /**
     * Durée estimée pour répondre en secondes
     * FACILE : 30s / MOYEN : 60s / DIFFICILE : 90s
     */
    @Column(name = "duree_secondes", nullable = false)
    private Integer dureeSecondes;

    /**
     * true  → question visible et utilisable dans les sessions
     * false → question désactivée par l'admin (brouillon ou obsolète)
     */
    @Column(nullable = false)
    private boolean actif = true;

    /**
     * Date de création — traçabilité
     */
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    /**
     * Compétences évaluées par cette question.
     *
     * ManyToMany car une question peut toucher
     * plusieurs compétences à la fois.
     *
     * Exemple :
     *   "Créez une procédure qui retourne les étudiants > 70%"
     *   → Compétence "SQL complexes"
     *   → Compétence "Procédures stockées PL/SQL"
     */
    @ManyToMany
    @JoinTable(
        name = "questions_competences",
        joinColumns        = @JoinColumn(name = "question_id"),
        inverseJoinColumns = @JoinColumn(name = "competence_id")
    )
    @Builder.Default
    private List<Competence> competences = new ArrayList<>();

    /**
     * Choix de réponse associés à cette question.
     * Structure variable selon le type :
     *   QCM_SIMPLE   → 4 choix, 1 correct
     *   QCM_MULTIPLE → 4 choix, N corrects
     *   VRAI_FAUX    → 2 choix
     *   ORDRE        → N choix ordonnés
     *   APPARIEMENT  → paires gauche/droite
     *   TEXTE_TROU   → 1 choix = réponse attendue
     */
    @OneToMany(
        mappedBy = "question",
        cascade  = CascadeType.ALL,
        fetch    = FetchType.LAZY,
        orphanRemoval = true
    )
    @Builder.Default
    private List<Choix> choix = new ArrayList<>();

    @PrePersist
    private void prePersist() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
        validerCoherence();
    }

    @PreUpdate
    private void preUpdate() {
        validerCoherence();
    }

    /**
     * Validation métier : une question doit avoir au moins une compétence
     */
    private void validerCoherence() {
        if (competences == null || competences.isEmpty()) {
            throw new IllegalStateException(
                "Une question doit être liée à au moins une compétence");
        }

        if (dureeSecondes == null) {
            dureeSecondes = switch (difficulte) {
                case FACILE    -> 30;
                case MOYEN     -> 60;
                case DIFFICILE -> 90;
            };
        }
    }
}

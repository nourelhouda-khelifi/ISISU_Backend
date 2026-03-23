package com.example.demo.questions.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Choix de réponse d'une question.
 *
 * La structure s'adapte à chaque type de question :
 *
 * QCM_SIMPLE / QCM_MULTIPLE :
 *   contenu    = texte du choix ("HAVING", "WHERE"...)
 *   estCorrect = true si bonne réponse
 *   ordre      = position d'affichage (A, B, C, D)
 *
 * VRAI_FAUX :
 *   contenu    = "Vrai" ou "Faux"
 *   estCorrect = true pour la bonne option
 *   ordre      = 1 ou 2
 *
 * ORDRE :
 *   contenu    = élément à ordonner ("Couche Physique"...)
 *   estCorrect = toujours true (tous les éléments sont bons)
 *   ordre      = position correcte (1, 2, 3, 4...)
 *
 * APPARIEMENT :
 *   contenu    = texte gauche ou droite ("Héritage", "extends"...)
 *   estCorrect = toujours true
 *   ordre      = identifiant de la paire (1=gauche+droite liés)
 *
 * TEXTE_TROU :
 *   contenu    = réponse attendue ("WHERE", "extends"...)
 *   estCorrect = true
 *   ordre      = 1 (un seul choix)
 */
@Entity
@Table(name = "choix")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Choix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Contenu du choix
     * Exemple : "HAVING", "Vrai", "Couche Physique", "WHERE"
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    /**
     * Indique si ce choix est correct.
     *
     * QCM_SIMPLE   → true sur 1 seul choix
     * QCM_MULTIPLE → true sur plusieurs choix
     * VRAI_FAUX    → true sur 1 seul choix
     * ORDRE        → true sur tous (l'ordre fait la correction)
     * APPARIEMENT  → true sur tous (les paires font la correction)
     * TEXTE_TROU   → true sur le seul choix
     */
    @Column(name = "est_correct", nullable = false)
    private boolean estCorrect;

    /**
     * Ordre / position du choix.
     *
     * QCM / VRAI_FAUX  → ordre d'affichage (1, 2, 3, 4)
     * ORDRE            → position correcte dans la séquence
     * APPARIEMENT      → identifiant de la paire
     * TEXTE_TROU       → toujours 1
     */
    @Column(nullable = false)
    private Integer ordre;

    /**
     * Question à laquelle appartient ce choix
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
}

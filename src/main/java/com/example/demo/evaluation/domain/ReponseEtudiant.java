package com.example.demo.evaluation.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * ReponseEtudiant — Enregistrement d'une réponse à une question
 * 
 * Même si l'étudiant répond à une question (QuestionSession),
 * on crée un ReponseEtudiant pour tracer:
 * - Quelle réponse a-t-il donné?
 * - Est-ce correct?
 * - Combien de temps a-t-il pris?
 */
@Entity
@Table(name = "reponse_etudiant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReponseEtudiant {
    
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
     * Question répondue
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_session_id", nullable = false)
    private QuestionSession questionSession;
    
    /**
     * Pour QCM: liste des IDs de choix sélectionnés (JSON)
     * Format: [1, 3, 5]
     */
    @Column(columnDefinition = "TEXT", name = "choix_selectionnes_json")
    private String choixSelectionnesJSON;
    
    /**
     * Pour TEXTE_TROU: réponse texte libre
     */
    @Column(columnDefinition = "TEXT", name = "reponse_texte")
    private String reponseTexte;
    
    /**
     * La réponse est-elle correcte?
     */
    @Column(nullable = false, name = "est_correcte")
    private Boolean estCorrecte;
    
    /**
     * Temps de réaction en secondes
     * (pour analyser la vitesse de réponse — peut identifier le stress/confusion)
     */
    @Column(name = "duree_reaction_secondes")
    private Integer dureeReactionSecondes;
    
    /**
     * Timestamp de la réponse
     */
    @Column(nullable = false, name = "date_reponse")
    private LocalDateTime dateReponse;
}

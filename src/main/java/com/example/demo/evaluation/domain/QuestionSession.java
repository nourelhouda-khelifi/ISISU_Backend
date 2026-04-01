package com.example.demo.evaluation.domain;

import com.example.demo.evaluation.domain.enums.TypeQSession;
import com.example.demo.questions.domain.Question;
import jakarta.persistence.*;
import lombok.*;

/**
 * QuestionSession — Représente une question pré-sélectionnée pour une session
 * 
 * Les questions sont pré-sélectionnées AVANT que l'étudiant commence à répondre.
 * Cela permet:
 * - D'éviter les requêtes en temps réel (plus rapide)
 * - De tracer exactement quelles questions ont été proposées
 * - De garantir la continuité si la session se ferme/réouvre
 */
@Entity
@Table(name = "question_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Session à laquelle appartient cette question
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private SessionTest session;
    
    /**
     * La question elle-même
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    /**
     * Ordre d'apparition dans la session
     * (pour pouvoir lister les questions dans l'ordre)
     */
    @Column(nullable = false)
    private Integer ordre;
    
    /**
     * Type de question:
     * - NORMALE: question posée dans le flux normal
     * - CONFIRMATION: deuxième question FACILE pour confirmer une lacune
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeQSession type = TypeQSession.NORMALE;
    
    /**
     * Flagi: a-t-elle reçu une réponse?
     */
    @Column(nullable = false, name = "est_repondue")
    private Boolean estRepondue = false;
    
    /**
     * Flagi: la réponse est-elle correcte? (null si pas encore répondue)
     */
    @Column(name = "est_correcte")
    private Boolean estCorrecte;
    
    /**
     * Flag: a-t-elle été une confirmation qui a échoué?
     * Utilisé pour détecter les lacunes confirmées (2 échecs MOYEN/DIFFICILE)
     */
    @Column(nullable = false, name = "confirmation_failed")
    private Boolean confirmationFailed = false;
}

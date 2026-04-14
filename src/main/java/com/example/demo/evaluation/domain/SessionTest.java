package com.example.demo.evaluation.domain;

import com.example.demo.auth.domain.Utilisateur;
import com.example.demo.evaluation.domain.enums.StatutSession;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SessionTest — Représente une session d'évaluation d'un étudiant
 * 
 * Une session = une tentative complète d'évaluation sur les 11 modules
 * Chaque étudiant peut faire plusieurs sessions
 */
@Entity
@Table(name = "session_test")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionTest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * L'étudiant qui passe le test
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;
    
    /**
     * Date/heure de démarrage du test
     */
    @Column(nullable = false)
    private LocalDateTime dateDebut;
    
    /**
     * Date/heure de fin (null si encore en cours)
     */
    @Column(name = "date_fin")
    private LocalDateTime dateFin;
    
    /**
     * Statut de la session: EN_COURS, TERMINEE, ABANDONNEE, TIMEOUT
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutSession statut = StatutSession.EN_COURS;
    
    /**
     * Durée max en secondes (2h = 7200s)
     */
    @Column(nullable = false)
    private Integer dureeMaxSecondes = 7200;
    
    /**
     * Numéro de session pour cet étudiant (1ère, 2ème, 3ème tentative)
     */
    @Column(nullable = false)
    private Integer numeroSession = 1;
    
    /**
     * Ordre des 11 modules calculé au démarrage (JSON pour traçabilité)
     * Format: ["E3-1-IN-1 (POO Java)", "E3-1-IN-3 (Gestion projet)", ...]
     */
    @Column(columnDefinition = "TEXT", name = "ordre_modules_json")
    private String ordreModulesJSON;
    
    /**
     * Raison de fermeture si applicable: "timeout", "utilisateur", "inactivite"
     */
    @Column(length = 100)
    private String raison;
    
    /**
     * Date d'abandon si applicable
     */
    @Column(name = "date_abandon")
    private LocalDateTime dateAbandon;
    
    /**
     * Questions pré-sélectionnées pour cette session
     * ~400 questions : 11 modules × 42 compétences ÷ 1 compétence par question (~1 par module)
     * × 3 FACILE + 3 MOYEN + 3 DIFFICILE
     */
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<QuestionSession> questionSessions = new ArrayList<>();
    
    /**
     * Réponses de l'étudiant aux questions
     */
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<ReponseEtudiant> reponses = new ArrayList<>();
    
    /**
     * Scores finaux par compétence
     */
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<ScoreCompetence> scores = new ArrayList<>();
    
    /**
     * Calculer le temps écoulé en secondes
     */
    public Integer getTempsEcoulesSecondes() {
        LocalDateTime fin = dateFin != null ? dateFin : LocalDateTime.now();
        long difference = java.time.temporal.ChronoUnit.SECONDS.between(dateDebut, fin);
        return Math.toIntExact(difference);
    }
    
    /**
     * Calculer le temps restant en secondes
     */
    public Integer getTempsRestantSecondes() {
        int tempsEcoulé = getTempsEcoulesSecondes();
        int tempsRestant = dureeMaxSecondes - tempsEcoulé;
        return Math.max(0, tempsRestant);
    }
    
    /**
     * Vérifier si le timer a expiré
     */
    public boolean isTimerExpire() {
        return getTempsRestantSecondes() <= 0;
    }
}

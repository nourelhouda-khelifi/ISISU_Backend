package com.example.demo.evaluation.service;

import com.example.demo.evaluation.domain.*;
import com.example.demo.evaluation.domain.enums.NiveauAtteint;
import com.example.demo.evaluation.domain.enums.StatutCompetence;
import com.example.demo.evaluation.repository.ReponseEtudiantRepository;
import com.example.demo.evaluation.repository.ScoreCompetenceRepository;
import com.example.demo.questions.domain.enums.NiveauDifficulte;
import com.example.demo.questions.domain.Question;
import com.example.demo.referentiel.domain.Competence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ScoringService — Calcule les scores finaux pour chaque compétence
 * 
 * Formule de pondération:
 *   scoreObtenu = Σ(correcte × pondération) / Σ(pondérations totales)
 * 
 * Pondération:
 *   FACILE    = 1.0
 *   MOYEN     = 1.5
 *   DIFFICILE = 2.0
 * 
 * Statut déterminé par niveau atteint:
 *   NON_DEMARRE  → LACUNE (0%)
 *   FACILE       → LACUNE si échoué, A_RENFORCER si réussi
 *   MOYEN        → A_RENFORCER si échoué, ACQUIS si réussi
 *   DIFFICILE    → ACQUIS si échoué, MAITRISE si réussi
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScoringService {
    
    private final ReponseEtudiantRepository reponseRepository;
    private final ScoreCompetenceRepository scoreRepository;
    private static final double FACILE_WEIGHT = 1.0;
    private static final double MOYEN_WEIGHT = 1.5;
    private static final double DIFFICILE_WEIGHT = 2.0;
    
    /**
     * Calculer tous les scores pour une session terminée
     * 
     * @param session Session avec reponses déjà remplies
     * @return Liste de ScoreCompetence (non sauvegardés)
     */
    public List<ScoreCompetence> calculateAllScores(SessionTest session) {
        List<ScoreCompetence> scores = new ArrayList<>();
        
        // 1. Grouper réponses par compétence
        Map<Competence, List<ReponseEtudiant>> reponsesByCompetence = 
            groupReponsesByCompetence(session);
        
        log.info("Session {}: Calcul scores pour {} compétences", 
            session.getId(), reponsesByCompetence.size()
        );
        
        // 2. Pour chaque compétence, calculer le score
        for (Map.Entry<Competence, List<ReponseEtudiant>> entry : 
            reponsesByCompetence.entrySet()) {
            
            Competence competence = entry.getKey();
            List<ReponseEtudiant> reponses = entry.getValue();
            
            ScoreCompetence score = calculateScoreForCompetence(
                session, competence, reponses
            );
            
            scores.add(score);
        }
        
        // 3. Charger scores précédents et calculer évolution
        ScoreCompetence lastSession = getLastSessionForUser(session);
        if (lastSession != null) {
            enrichiseWithEvolution(scores, lastSession);
        }
        
        return scores;
    }
    
    /**
     * Calculer le score pour UNE compétence
     */
    private ScoreCompetence calculateScoreForCompetence(
        SessionTest session,
        Competence competence,
        List<ReponseEtudiant> reponses
    ) {
        // Filtrer par compétence
        List<ReponseEtudiant> competenceReponses = reponses.stream()
            .filter(r -> r.getQuestionSession().getQuestion()
                .getCompetences().contains(competence))
            .collect(Collectors.toList());
        
        if (competenceReponses.isEmpty()) {
            // Pas de questions posées pour cette compétence
            return ScoreCompetence.builder()
                .session(session)
                .competence(competence)
                .scoreObtenu(0.0)
                .statut(StatutCompetence.LACUNE)
                .niveauAtteint(NiveauAtteint.NON_DEMARRE)
                .nbQuestions(0)
                .nbBonnesReponses(0)
                .build();
        }
        
        // Calculer score avec pondération
        double scoreObtenu = calculatePonderatedScore(competenceReponses);
        
        // Déterminer le niveau atteint (max difficulty reached)
        NiveauAtteint niveauAtteint = determineMaxNiveauReached(competenceReponses);
        
        // Déterminer le statut basé sur niveau+score
        StatutCompetence statut = mapStatut(niveauAtteint, scoreObtenu, competenceReponses);
        
        // Compter bonnes réponses
        long bonnesReponses = competenceReponses.stream()
            .filter(ReponseEtudiant::getEstCorrecte)
            .count();
        
        return ScoreCompetence.builder()
            .session(session)
            .competence(competence)
            .scoreObtenu(scoreObtenu)
            .statut(statut)
            .niveauAtteint(niveauAtteint)
            .nbQuestions(competenceReponses.size())
            .nbBonnesReponses((int) bonnesReponses)
            .build();
    }
    
    /**
     * Calculer score avec pondération
     * 
     * scoreObtenu = Σ(correcte × pondération) / Σ(pondérations totales)
     */
    private double calculatePonderatedScore(List<ReponseEtudiant> reponses) {
        double totalPointsObtenu = 0.0;
        double totalPointsPossible = 0.0;
        
        for (ReponseEtudiant r : reponses) {
            NiveauDifficulte difficulte = r.getQuestionSession().getQuestion()
                .getDifficulte();
            double weight = getWeight(difficulte);
            
            totalPointsPossible += weight;
            
            if (r.getEstCorrecte()) {
                totalPointsObtenu += weight;
            }
        }
        
        if (totalPointsPossible == 0) {
            return 0.0;
        }
        
        double score = totalPointsObtenu / totalPointsPossible;
        // Arrondir à 2 décimales
        return Math.round(score * 1000.0) / 1000.0;
    }
    
    /**
     * Obtenir le poids selon la pondération
     */
    private double getWeight(NiveauDifficulte difficulte) {
        return switch (difficulte) {
            case FACILE -> FACILE_WEIGHT;
            case MOYEN -> MOYEN_WEIGHT;
            case DIFFICILE -> DIFFICILE_WEIGHT;
        };
    }
    
    /**
     * Déterminer le niveau MAX atteint par l'étudiant
     * Pour cette compétence (FACILE → MOYEN → DIFFICILE)
     */
    private NiveauAtteint determineMaxNiveauReached(List<ReponseEtudiant> reponses) {
        NiveauAtteint maxNiveau = NiveauAtteint.NON_DEMARRE;
        
        for (ReponseEtudiant r : reponses) {
            NiveauDifficulte p = r.getQuestionSession().getQuestion().getDifficulte();
            NiveauAtteint niveau = switch (p) {
                case FACILE -> NiveauAtteint.FACILE;
                case MOYEN -> NiveauAtteint.MOYEN;
                case DIFFICILE -> NiveauAtteint.DIFFICILE;
            };
            
            // Gardé le max atteint
            if (niveau.ordinal() > maxNiveau.ordinal()) {
                maxNiveau = niveau;
            }
        }
        
        return maxNiveau;
    }
    
    /**
     * Mapper le statut basé sur le niveau et les résultats
     * 
     * Logique:
     * - NON_DEMARRE → LACUNE
     * - FACILE: réussi → A_RENFORCER, échoué → LACUNE
     * - MOYEN: réussi → ACQUIS, échoué → A_RENFORCER
     * - DIFFICILE: réussi → MAITRISE, échoué → ACQUIS
     */
    private StatutCompetence mapStatut(
        NiveauAtteint niveau,
        double scoreObtenu,
        List<ReponseEtudiant> reponses
    ) {
        if (niveau == NiveauAtteint.NON_DEMARRE) {
            return StatutCompetence.LACUNE;
        }
        
        // Déterminer la réussite au dernier niveau
        boolean reussiLastLevel = hasSucceededAtLevel(niveau, reponses);
        
        return switch (niveau) {
            case FACILE -> 
                reussiLastLevel ? StatutCompetence.A_RENFORCER : StatutCompetence.LACUNE;
            case MOYEN -> 
                reussiLastLevel ? StatutCompetence.ACQUIS : StatutCompetence.A_RENFORCER;
            case DIFFICILE -> 
                reussiLastLevel ? StatutCompetence.MAITRISE : StatutCompetence.ACQUIS;
            case NON_DEMARRE -> StatutCompetence.LACUNE;
        };
    }
    
    /**
     * Vérifier si l'étudiant a réussi au niveau spécifié
     */
    private boolean hasSucceededAtLevel(
        NiveauAtteint niveau,
        List<ReponseEtudiant> reponses
    ) {
        NiveauDifficulte targetDifficulte = switch (niveau) {
            case FACILE -> NiveauDifficulte.FACILE;
            case MOYEN -> NiveauDifficulte.MOYEN;
            case DIFFICILE -> NiveauDifficulte.DIFFICILE;
            case NON_DEMARRE -> null;
        };
        
        return reponses.stream()
            .filter(r -> r.getQuestionSession().getQuestion()
                .getDifficulte() == targetDifficulte)
            .anyMatch(ReponseEtudiant::getEstCorrecte);
    }
    
    /**
     * Grouper réponses par compétence
     */
    private Map<Competence, List<ReponseEtudiant>> groupReponsesByCompetence(
        SessionTest session
    ) {
        return session.getReponses().stream()
            .collect(Collectors.groupingBy(
                r -> r.getQuestionSession().getQuestion().getCompetences()
                    .stream().findFirst().orElse(null)
            ))
            .entrySet().stream()
            .filter(e -> e.getKey() != null)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    /**
     * Récupérer la dernière session terminée précédente
     */
    private ScoreCompetence getLastSessionForUser(SessionTest currentSession) {
        // À implémenter: chercher dernière session TERMINEE de l'utilisateur
        // avant la session actuelle
        return null;
    }
    
    /**
     * Enrichir scores avec évolution par rapport session précédente
     */
    private void enrichiseWithEvolution(
        List<ScoreCompetence> scores,
        ScoreCompetence lastSession
    ) {
        // À implémenter: calculer scorePrecedent et evolutionPourcentage
    }
}

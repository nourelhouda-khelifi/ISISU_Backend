package com.example.demo.evaluation.service;

import com.example.demo.evaluation.domain.*;
import com.example.demo.evaluation.domain.enums.NiveauAtteint;
import com.example.demo.evaluation.domain.enums.TypeQSession;
import com.example.demo.evaluation.repository.QuestionSessionRepository;
import com.example.demo.evaluation.repository.ReponseEtudiantRepository;
import com.example.demo.questions.domain.enums.NiveauDifficulte;
import com.example.demo.questions.domain.Question;
import com.example.demo.referentiel.domain.Competence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AlgorithmeAdaptatifService — Logique adaptative du test
 * 
 * Flux: FACILE → MOYEN → DIFFICILE (par compétence)
 * 
 * Double confirmation pour lacunes:
 * - Si 2 FACILE échoués consécutifs → LACUNE confirmée
 * - Ajouter 1 question FACILE de confirmation
 * 
 * Progression intelligente:
 * - FACILE réussi + FACILE confirmation réussi → continuer MOYEN
 * - FACILE échoué + confirmation échoué → LACUNE, passer au suivant
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlgorithmeAdaptatifService {
    
    private final QuestionSessionRepository questionSessionRepository;
    private final ReponseEtudiantRepository reponseRepository;
    private final QuestionSelectionService questionSelectionService;
    
    /**
     * Analyser la réponse à la question courante et déterminer la prochaine
     * 
     * Retourne:
     * - La prochaine QuestionSession à proposer
     * - OU null si session terminée
     */
    public QuestionSession analyzeResponseAndGetNextQuestion(
        SessionTest session,
        QuestionSession currentQuestion,
        ReponseEtudiant response
    ) {
        Competence competence = currentQuestion.getQuestion()
            .getCompetences().stream().findFirst().orElse(null);
        
        if (competence == null) {
            log.warn("Question sans compétence: {}", currentQuestion.getId());
            return getNextQuestionByOrder(session, currentQuestion.getOrdre());
        }
        
        NiveauDifficulte currentLevel = currentQuestion.getQuestion().getDifficulte();
        
        // LOGIQUE ADAPTATIVE
        if (response.getEstCorrecte()) {
            // ✅ L'étudiant a réussi
            return handleSuccessfulResponse(session, currentQuestion, competence, currentLevel);
        } else {
            // ❌ L'étudiant a échoué
            return handleFailedResponse(session, currentQuestion, competence, currentLevel);
        }
    }
    
    /**
     * Gérer une réponse CORRECTE
     * 
     * Progression: FACILE ✅ → proposer MOYEN
     *              MOYEN ✅ → proposer DIFFICILE
     *              DIFFICILE ✅ → passer compétence suivante
     */
    private QuestionSession handleSuccessfulResponse(
        SessionTest session,
        QuestionSession currentQuestion,
        Competence competence,
        NiveauDifficulte currentLevel
    ) {
        if (currentQuestion.getType() == TypeQSession.CONFIRMATION) {
            // Confirmation FACILE réussie après échec initial
            log.debug("Confirmation réussie pour {}", competence.getIntitule());
            // Continuer avec MOYEN pour cette compétence
            return moveToNextLevel(session, competence, currentLevel);
        }
        
        // Progression normal: FACILE → MOYEN, MOYEN → DIFFICILE
        NiveauDifficulte nextLevel = getNextLevel(currentLevel);
        
        if (nextLevel != null) {
            log.debug("{} réussi pour {}, proposer {}", 
                currentLevel, competence.getIntitule(), nextLevel);
            return findQuestionForCompetenceAtLevel(session, competence, nextLevel);
        } else {
            // DIFFICILE réussi → compétence maîtrisée, passer au suivant
            log.debug("DIFFICILE réussi pour {}, compétence complète", competence.getIntitule());
            return getNextUnansweredQuestion(session);
        }
    }
    
    /**
     * Gérer une réponse ÉCHOUÉE
     * 
     * CORRECTION: Pas de downgrade après validation des bases
     * - FACILE échoué → proposer FACILE de confirmation
     * - Confirmation échouée → LACUNE confirmée, passer suivant
     * - MOYEN échoué → proposer MOYEN de confirmation (pas progression)
     * - DIFFICILE échoué → proposer DIFFICILE de confirmation (pas régression)
     */
    private QuestionSession handleFailedResponse(
        SessionTest session,
        QuestionSession currentQuestion,
        Competence competence,
        NiveauDifficulte currentLevel
    ) {
        if (currentQuestion.getType() == TypeQSession.CONFIRMATION) {
            // Confirmation échouée
            log.debug("Confirmation ÉCHOUÉE pour {} → LACUNE confirmée", 
                competence.getIntitule());
            // Marquer qu'une confirmation a échoué
            currentQuestion.setConfirmationFailed(true);
            questionSessionRepository.save(currentQuestion);
            // LACUNE confirmée, passer au suivant
            return getNextUnansweredQuestion(session);
        }
        
        // Tous les niveaux échoués (FACILE, MOYEN, DIFFICILE) → Ajouter confirmation
        log.debug("{} échoué pour {} → Demander confirmation au même niveau", 
            currentLevel, competence.getIntitule());
        
        int nextOrdre = (int) session.getQuestionSessions()
            .stream()
            .mapToInt(QuestionSession::getOrdre)
            .max()
            .orElse(0) + 1;
        
        try {
            QuestionSession confirmation = questionSelectionService
                .addConfirmationQuestion(session, competence, currentLevel, nextOrdre);
            return confirmation;
        } catch (Exception e) {
            log.warn("Impossible d'ajouter question confirmation: {}", e.getMessage());
            return getNextUnansweredQuestion(session);
        }
    }
    
    /**
     * Obtenir le niveau suivant dans la progression
     */
    private NiveauDifficulte getNextLevel(NiveauDifficulte current) {
        return switch (current) {
            case FACILE -> NiveauDifficulte.MOYEN;
            case MOYEN -> NiveauDifficulte.DIFFICILE;
            case DIFFICILE -> null;  // Fin de progression
        };
    }
    
    /**
     * Passer au niveau suivant pour une compétence
     */
    private QuestionSession moveToNextLevel(
        SessionTest session,
        Competence competence,
        NiveauDifficulte currentLevel
    ) {
        NiveauDifficulte nextLevel = getNextLevel(currentLevel);
        
        if (nextLevel == null) {
            return getNextUnansweredQuestion(session);
        }
        
        return findQuestionForCompetenceAtLevel(session, competence, nextLevel);
    }
    
    /**
     * Trouver une question pour une compétence à un niveau spécifique
     */
    private QuestionSession findQuestionForCompetenceAtLevel(
        SessionTest session,
        Competence competence,
        NiveauDifficulte level
    ) {
        return session.getQuestionSessions().stream()
            .filter(qs -> !qs.getEstRepondue() &&
                qs.getType() == TypeQSession.NORMALE &&
                qs.getQuestion().getCompetences().contains(competence) &&
                qs.getQuestion().getDifficulte() == level)
            .findFirst()
            .orElseGet(() -> getNextUnansweredQuestion(session));
    }
    
    /**
     * Récupérer la prochaine question non répondue
     * (ordre des questions + 1)
     */
    private QuestionSession getNextUnansweredQuestion(SessionTest session) {
        return session.getQuestionSessions().stream()
            .filter(qs -> !qs.getEstRepondue())
            .min(Comparator.comparingInt(QuestionSession::getOrdre))
            .orElse(null);  // null = fin de session
    }
    
    /**
     * Récupérer la question suivante par ordre (sans logique adaptative)
     */
    private QuestionSession getNextQuestionByOrder(SessionTest session, int currentOrdre) {
        return session.getQuestionSessions().stream()
            .filter(qs -> qs.getOrdre() > currentOrdre)
            .min(Comparator.comparingInt(QuestionSession::getOrdre))
            .orElse(null);
    }
    
    /**
     * Compter les lacunes détectées pour la session
     */
    public int countDetectedLacunes(SessionTest session) {
        return (int) session.getQuestionSessions().stream()
            .filter(qs -> qs.getType() == TypeQSession.CONFIRMATION)
            .filter(QuestionSession::getEstRepondue)
            .filter(qs -> !qs.getEstCorrecte())
            .count();
    }
    
    /**
     * Compter les compétences complètement évaluées
     */
    public int countCompletedCompetences(SessionTest session) {
        return (int) session.getQuestionSessions().stream()
            .filter(QuestionSession::getEstRepondue)
            .map(qs -> qs.getQuestion().getCompetences().stream().findFirst().orElse(null))
            .filter(Objects::nonNull)
            .distinct()
            .count();
    }
}

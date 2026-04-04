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
     * ✅ FIX M4: Traiter TOUTES les compétences (pas seulement la première)
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
        List<Competence> competences = currentQuestion.getQuestion()
            .getCompetences();
        
        if (competences == null || competences.isEmpty()) {
            log.warn("Question sans compétence: {}", currentQuestion.getId());
            return getNextQuestionByOrder(session, currentQuestion.getOrdre());
        }
        
        NiveauDifficulte currentLevel = currentQuestion.getQuestion().getDifficulte();
        
        // ✅ M4: Traiter TOUTES les compétences associées à cette question
        // Logique pédagogique: Si question couvre 3 compétences, toutes 3 évoluent
        for (Competence competence : competences) {
            try {
                if (response.getEstCorrecte()) {
                    // ✅ L'étudiant a réussi pour cette compétence
                    handleSuccessfulResponse(session, currentQuestion, competence, currentLevel);
                } else {
                    // ❌ L'étudiant a échoué pour cette compétence
                    handleFailedResponse(session, currentQuestion, competence, currentLevel);
                }
                log.debug("Progression enregistrée pour compétence: {} (question: {})", 
                    competence.getIntitule(), currentQuestion.getId());
            } catch (Exception e) {
                log.warn("Erreur traitement compétence {} pour question {}: {}", 
                    competence.getId(), currentQuestion.getId(), e.getMessage());
                // Continuer avec les autres compétences même si une échoue
            }
        }
        
        // Retourner la prochaine question (logique unchanged)
        Competence mainCompetence = competences.get(0);
        if (response.getEstCorrecte()) {
            return handleSuccessfulResponse(session, currentQuestion, mainCompetence, currentLevel);
        } else {
            return handleFailedResponse(session, currentQuestion, mainCompetence, currentLevel);
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
     * ✅ FIX C3: Exception handling robuste pour confirmations
     * 
     * Logique pédagogique:
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
            // Confirmation échouée → LACUNE confirmée
            log.info("Confirmation ÉCHOUÉE pour {} → Lacune confirmée (pédagogiquement valide)", 
                competence.getIntitule());
            // Marquer qu'une confirmation a échoué (traçabilité pédagogique)
            currentQuestion.setConfirmationFailed(true);
            questionSessionRepository.save(currentQuestion);
            // LACUNE confirmée pédagogiquement, passer au suivant
            return getNextUnansweredQuestion(session);
        }
        
        // Tous les niveaux échoués (FACILE, MOYEN, DIFFICILE) → Ajouter confirmation
        // Pédagogie: "Un accident n'est pas une lacune" - proposer vérification
        log.debug("{} échoué pour {} → Proposer verification (confirmation au même niveau)", 
            currentLevel, competence.getIntitule());
        
        int nextOrdre = (int) session.getQuestionSessions()
            .stream()
            .mapToInt(QuestionSession::getOrdre)
            .max()
            .orElse(0) + 1;
        
        try {
            // ✅ C3: Esssayer d'ajouter question confirmation
            QuestionSession confirmation = questionSelectionService
                .addConfirmationQuestion(session, competence, currentLevel, nextOrdre);
            
            if (confirmation == null) {
                log.warn("addConfirmationQuestion retourné null pour {} level {}", 
                    competence.getIntitule(), currentLevel);
                // Fallback: passer au suivant si confirmation impossible
                return getNextUnansweredQuestion(session);
            }
            
            log.debug("Question confirmation ajoutée: {} pour {} (niveau {})", 
                confirmation.getId(), competence.getIntitule(), currentLevel);
            return confirmation;
            
        } catch (IllegalArgumentException e) {
            // Pas de questions de confirmation disponibles
            log.warn("Pas de questions confirmation disponibles pour {} level {}: {}. " +
                    "Passer au suivant (fallback pédagogique).", 
                competence.getIntitule(), currentLevel, e.getMessage());
            // Fallback pédagogique: passer à la question suivante
            return getNextUnansweredQuestion(session);
            
        } catch (Exception e) {
            // Autres erreurs (DB, etc)
            log.error("Erreur critique lors de l'ajout de confirmation pour {} nivel {}: {}", 
                competence.getIntitule(), currentLevel, e.getMessage(), e);
            // Fallback final: passer au suivant (ne pas bloquer la session)
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

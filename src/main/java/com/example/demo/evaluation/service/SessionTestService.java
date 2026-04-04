package com.example.demo.evaluation.service;

import com.example.demo.auth.domain.Utilisateur;
import com.example.demo.evaluation.domain.*;
import com.example.demo.evaluation.domain.enums.StatutSession;
import com.example.demo.evaluation.repository.*;
import com.example.demo.referentiel.infrastructure.ModuleFIERepository;
import com.example.demo.questions.domain.Choix;
import com.example.demo.questions.domain.Question;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * SessionTestService — Orchestration complète du lifecycle d'une session
 * 
 * Responsabilités:
 * 1. Créer nouvelle session (ordre modules + pré-sélection questions)
 * 2. Récupérer question courante
 * 3. Traiter réponse + calculer prochaine question (algorithme adaptatif)
 * 4. Terminer session et calculer scores
 * 5. Gérer timeouts et abandons
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SessionTestService {
    
    private final SessionTestRepository sessionRepository;
    private final QuestionSessionRepository questionSessionRepository;
    private final ReponseEtudiantRepository reponseRepository;
    private final ScoreCompetenceRepository scoreRepository;
    private final EntityManager entityManager;
    @SuppressWarnings("unused")
    private final ModuleFIERepository moduleFieRepository;
    
    private final ModuleOrderService moduleOrderService;
    private final QuestionSelectionService questionSelectionService;
    private final ScoringService scoringService;
    private final AlgorithmeAdaptatifService algorithmService;
    
    /**
     * ÉTAPE 1: Créer nouvelle session pour un étudiant
     */
    public SessionTest createNewSession(Utilisateur utilisateur) {
        log.info("Création session pour utilisateur: {}", utilisateur.getEmail());
        
        // 1. Créer la session
        int nextSessionNum = (int) sessionRepository
            .countByUtilisateurAndStatut(utilisateur, StatutSession.TERMINEE) + 1;
        
        SessionTest session = SessionTest.builder()
            .utilisateur(utilisateur)
            .dateDebut(LocalDateTime.now())
            .statut(StatutSession.EN_COURS)
            .numeroSession(nextSessionNum)
            .dureeMaxSecondes(7200)  // 2 heures
            .build();
        
        // 2. Calculer et stocker ordre des modules
        String ordreModules = moduleOrderService.calculateModuleOrder();
        session.setOrdreModulesJSON(ordreModules);
        
        // 3. Sauvegarder session
        session = sessionRepository.save(session);
        log.info("Session {} créée (numéro {})", session.getId(), nextSessionNum);
        
        // 4. Pré-sélectionner les questions
        List<QuestionSession> questionSessions = 
            questionSelectionService.selectQuestionsForSession(session);
        
        session.setQuestionSessions(questionSessions);
        session = sessionRepository.save(session);
        
        log.info("Session {}: {} questions sélectionnées", 
            session.getId(), questionSessions.size());
        
        return session;
    }
    
    /**
     * ÉTAPE 2: Récupérer la session courante d'un étudiant
     */
    public Optional<SessionTest> getCurrentSession(Utilisateur utilisateur) {
        Optional<SessionTest> session = sessionRepository
            .findFirstByUtilisateurAndStatutOrderByDateDebutDesc(utilisateur, StatutSession.EN_COURS);
        
        // Vérifier timeout
        if (session.isPresent() && session.get().isTimerExpire()) {
            log.warn("Session {} expirée (timeout)", session.get().getId());
            terminateSession(session.get(), StatutSession.TIMEOUT, "timeout");
            return Optional.empty();
        }
        
        return session;
    }
    
    /**
     * ÉTAPE 3: Récupérer la prochaine question non répondue
     */
    public Optional<QuestionSession> getNextQuestion(SessionTest session) {
        return session.getQuestionSessions().stream()
            .filter(q -> !q.getEstRepondue())
            .min(java.util.Comparator.comparingInt(QuestionSession::getOrdre));
    }
    
    /**
     * ÉTAPE 4: Traiter réponse d'étudiant
     */
    public void submitAnswer(
        SessionTest session,
        QuestionSession questionSession,
        ReponseEtudiant response
    ) {
        log.debug("Submission réponse pour question {} de session {}", 
            questionSession.getId(), session.getId());
        
        // 1. Valider réponse
        validateAnswer(response);
        
        // 2. Évaluer si la réponse est correcte
        evaluateAnswer(response, questionSession);
        
        // 3. Sauvegarder réponse
        response.setSession(session);
        response.setQuestionSession(questionSession);
        response.setDateReponse(LocalDateTime.now());
        reponseRepository.save(response);
        
        // 4. MAJ QuestionSession
        questionSession.setEstRepondue(true);
        questionSession.setEstCorrecte(response.getEstCorrecte());
        questionSessionRepository.save(questionSession);
        
        // 5. FLUSH pour s'assurer que les changes sont persistées
        entityManager.flush();
        
        // 6. Algorithme adaptatif: déterminer prochaine question
        //    (peut dynamiquement ajouter CONFIRMATION questions)
        QuestionSession nextQuestion = algorithmService
            .analyzeResponseAndGetNextQuestion(session, questionSession, response);
        
        // 7. Si plus de questions: terminer session
        if (nextQuestion == null) {
            log.info("Plus de questions pour session {}", session.getId());
            terminateSession(session, StatutSession.TERMINEE, null);
        }
    }
    
    /**
     * ÉTAPE 5: Terminer la session et calculer scores
     * 
     * ✅ FIX C2: Calculer scores pour TOUS statuts terminaux (TERMINEE, TIMEOUT, ABANDONNEE)
     * (Pédagogiquement: même si session timeout, on doit évaluer la progression)
     */
    public void terminateSession(
        SessionTest session,
        StatutSession statut,
        String raison
    ) {
        log.info("Fin session {}: statut={}, raison={}", 
            session.getId(), statut, raison);
        
        // 1. MAJ session
        session.setStatut(statut);
        session.setDateFin(LocalDateTime.now());
        if (raison != null) {
            session.setRaison(raison);
        }
        if (statut == StatutSession.ABANDONNEE) {
            session.setDateAbandon(LocalDateTime.now());
        }
        sessionRepository.save(session);
        
        // 2. Calculer tous les scores pour TOUS statuts terminaux
        // ✅ C2: Pas seulement TERMINEE, mais aussi TIMEOUT et ABANDONNEE
        // Logique pédagogique: évaluer la progression même si timeout
        if (statut != StatutSession.EN_COURS) {
            log.debug("Calculant scores pour session {} (statut={})", session.getId(), statut);
            List<ScoreCompetence> scores = scoringService.calculateAllScores(session);
            scores.forEach(s -> {
                s.calculerEvolution();
                scoreRepository.save(s);
            });
            
            log.info("Session {}: {} scores calculés (statut={})", 
                session.getId(), scores.size(), statut);
        }
    }
    
    /**
     * Récupérer historique d'une session
     */
    public Map<String, Object> getSessionSummary(SessionTest session) {
        Map<String, Object> summary = new HashMap<>();
        
        summary.put("sessionId", session.getId());
        summary.put("utilisateur", session.getUtilisateur().getEmail());
        summary.put("numeroSession", session.getNumeroSession());
        summary.put("statut", session.getStatut());
        summary.put("dateDebut", session.getDateDebut());
        summary.put("dateFin", session.getDateFin());
        summary.put("tempsEcoulesSecondes", session.getTempsEcoulesSecondes());
        summary.put("tempsRestantSecondes", session.getTempsRestantSecondes());
        
        // Stats
        summary.put("totalQuestions", session.getQuestionSessions().size());
        long repondues = session.getQuestionSessions().stream()
            .filter(QuestionSession::getEstRepondue)
            .count();
        summary.put("questionsRepondues", repondues);
        
        summary.put("nbLacunesDetectees", 
            algorithmService.countDetectedLacunes(session));
        summary.put("nbCompetencesCompletes", 
            algorithmService.countCompletedCompetences(session));
        
        // Scores
        List<ScoreCompetence> scores = session.getScores();
        summary.put("competencesEvaluees", scores.size());
        
        return summary;
    }
    
    /**
     * Récupérer résultats finals
     */
    public List<ScoreCompetence> getSessionResults(SessionTest session) {
        return scoreRepository.findBySessionOrderByCompetenceId(session);
    }
    
    /**
     * Abandonner une session
     */
    public void abandonSession(SessionTest session) {
        terminateSession(session, StatutSession.ABANDONNEE, "utilisateur");
    }
    
    /**
     * Valider la réponse (logique métier)
     */
    private void validateAnswer(ReponseEtudiant response) {
        // Valider que au moins une réponse a été donnée
        if (response.getChoixSelectionnesJSON() == null && 
            response.getReponseTexte() == null) {
            throw new IllegalArgumentException("Réponse vide");
        }
    }

    /**
     * Évaluer si la réponse est correcte
     * 
     * Compare les choix sélectionnés avec les bons choix du test
     */
    private void evaluateAnswer(ReponseEtudiant response, QuestionSession questionSession) {
        try {
            Question question = questionSession.getQuestion();
            
            // Cas 1: QCM ou APPARIEMENT avec choix
            if (response.getChoixSelectionnesJSON() != null && 
                !response.getChoixSelectionnesJSON().isEmpty()) {
                
                // Parser les IDs sélectionnés: "[15, 16, 17, 18, 19, 20]"
                String choixStr = response.getChoixSelectionnesJSON();
                List<Long> selectedIds = parseChoixIds(choixStr);
                
                // Récupérer tous les choix de la question
                List<Choix> allChoices = question.getChoix();
                
                if (allChoices == null || allChoices.isEmpty()) {
                    log.warn("Question {} sans choix", question.getId());
                    response.setEstCorrecte(false);
                    return;
                }
                
                // Pour APPARIEMENT: tous les choix doivent être sélectionnés (ordre n'importe pas)
                // Pour QCM_MULTIPLE: tous les choix doivent être corrects
                // Pour QCM_SIMPLE: exactement 1 choix et il doit être correct
                
                List<Choix> selectedChoices = allChoices.stream()
                    .filter(c -> selectedIds.contains(c.getId()))
                    .toList();
                
                // Vérifier si tous les choix sélectionnés sont marqués comme corrects
                boolean allCorrect = selectedChoices.stream()
                    .allMatch(Choix::isEstCorrect);
                
                // Vérifier aussi que le nombre de choix sélectionnés correspond aux choix corrects
                long correctCount = allChoices.stream()
                    .filter(Choix::isEstCorrect)
                    .count();
                
                boolean correctCount2 = selectedChoices.size() == correctCount;
                
                response.setEstCorrecte(allCorrect && correctCount2);
            } else {
                // Cas 2: Réponse textuelle
                // Pour MVP, considérer comme correcte si réponse fournie
                response.setEstCorrecte(response.getReponseTexte() != null && 
                    !response.getReponseTexte().trim().isEmpty());
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'évaluation de la réponse", e);
            response.setEstCorrecte(false);
        }
    }

    /**
     * Parser la chaîne JSON des IDs de choix: "[15, 16, 17, 18, 19, 20]"
     */
    private List<Long> parseChoixIds(String choixStr) {
        try {
            String cleaned = choixStr.replaceAll("[\\[\\]\\s]", "");
            if (cleaned.isEmpty()) return new ArrayList<>();
            
            return java.util.Arrays.stream(cleaned.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();
        } catch (Exception e) {
            log.warn("Erreur parsing choix: {}", choixStr, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Récupérer historique de l'étudiant
     */
    public List<SessionTest> getUserSessions(Utilisateur utilisateur) {
        return sessionRepository.findByUtilisateurOrderByDateDebutDesc(utilisateur);
    }
    
    /**
     * Vérifier s'il y a une session en cours
     */
    public boolean hasActiveSession(Utilisateur utilisateur) {
        return sessionRepository
            .findFirstByUtilisateurAndStatutOrderByDateDebutDesc(utilisateur, StatutSession.EN_COURS)
            .isPresent();
    }

    /**
     * Récupérer une session par son ID
     */
    public SessionTest getSessionById(Long sessionId) {
        return sessionRepository.findById(sessionId).orElse(null);
    }
}

package com.example.demo.evaluation.service;

import com.example.demo.evaluation.domain.QuestionSession;
import com.example.demo.evaluation.domain.SessionTest;
import com.example.demo.evaluation.domain.enums.TypeQSession;
import com.example.demo.questions.domain.Question;
import com.example.demo.questions.domain.enums.NiveauDifficulte;
import com.example.demo.questions.repository.QuestionRepository;
import com.example.demo.referentiel.domain.Competence;
import com.example.demo.referentiel.infrastructure.CompetenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * QuestionSelectionService — Pré-sélectionne les questions pour une session
 * 
 * Stratégie:
 * - Pour chaque compétence (42 total)
 * - Sélectionner 1 question FACILE, 1 MOYEN, 1 DIFFICILE
 * - Total: ~126 questions (~3 par compétence)
 * 
 * Avantage: Pas besoin de requête DB en temps réel, traçabilité complète
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionSelectionService {
    
    private final QuestionRepository questionRepository;
    private final CompetenceRepository competenceRepository;
    
    /**
     * Pré-sélectionner les questions pour une session
     * 
     * @param session La session (avec ordreModulesJSON déjà défini)
     * @return Liste de QuestionSession (non sauvegardées, juste créées)
     */
    public List<QuestionSession> selectQuestionsForSession(SessionTest session) {
        // 1. Récupérer toutes les compétences
        List<Competence> allCompetences = competenceRepository.findAll();
        
        log.info("Sélection questions: {} compétences trouvées", allCompetences.size());
        
        List<QuestionSession> questionSessions = new ArrayList<>();
        int ordre = 1;
        
        // 2. Pour chaque compétence
        for (Competence competence : allCompetences) {
            // Récupérer questions pour cette compétence par difficulté
            List<Question> questionsFacile = getQuestionsByCompetenceAndDifficulte(
                competence, NiveauDifficulte.FACILE
            );
            List<Question> questionsMoyen = getQuestionsByCompetenceAndDifficulte(
                competence, NiveauDifficulte.MOYEN
            );
            List<Question> questionsDifficile = getQuestionsByCompetenceAndDifficulte(
                competence, NiveauDifficulte.DIFFICILE
            );
            
            // Sélectionner 1 aléatoirement de chaque niveau
            if (!questionsFacile.isEmpty()) {
                Question q = selectRandomQuestion(questionsFacile);
                questionSessions.add(createQuestionSession(
                    session, q, ordre++, TypeQSession.NORMALE
                ));
            }
            
            if (!questionsMoyen.isEmpty()) {
                Question q = selectRandomQuestion(questionsMoyen);
                questionSessions.add(createQuestionSession(
                    session, q, ordre++, TypeQSession.NORMALE
                ));
            }
            
            if (!questionsDifficile.isEmpty()) {
                Question q = selectRandomQuestion(questionsDifficile);
                questionSessions.add(createQuestionSession(
                    session, q, ordre++, TypeQSession.NORMALE
                ));
            }
        }
        
        log.info("Session {}: {} questions sélectionnées", 
            session.getId(), questionSessions.size()
        );
        
        return questionSessions;
    }
    
    /**
     * Ajouter une question de confirmation au même niveau de difficulté
     * (utilisé par AlgorithmeAdaptatif quand une question est échouée)
     * 
     * IMPORTANTE: Depuis v12, les confirmations sont au MÊME niveau que la question échouée
     * - FACILE échoué → confirmation FACILE
     * - MOYEN échoué → confirmation MOYEN
     * - DIFFICILE échoué → confirmation DIFFICILE
     * 
     * @param session La session
     * @param competence La compétence où la question a échoué
     * @param difficulte Le niveau de difficulté de la question échouée
     * @param nextOrdre L'ordre d'insertion
     * @return QuestionSession de confirmation au même niveau
     */
    public QuestionSession addConfirmationQuestion(
        SessionTest session, 
        Competence competence,
        NiveauDifficulte difficulte,
        int nextOrdre
    ) {
        List<Question> confirmationQuestions = getQuestionsByCompetenceAndDifficulte(
            competence, difficulte
        );
        
        if (confirmationQuestions.isEmpty()) {
            throw new IllegalStateException(
                "Pas de question " + difficulte + " pour compétence: " + competence.getIntitule()
            );
        }
        
        Question q = selectRandomQuestion(confirmationQuestions);
        
        return createQuestionSession(
            session, q, nextOrdre, TypeQSession.CONFIRMATION
        );
    }
    
    /**
     * DEPRECATED: Ajouter une question de confirmation FACILE
     * Gardé pour compatibilité rétroactive, utilise addConfirmationQuestion(session, competence, niveau, ordre)
     */
    @Deprecated(since = "v12", forRemoval = false)
    public QuestionSession addConfirmationQuestion(
        SessionTest session, 
        Competence competence, 
        int nextOrdre
    ) {
        // Par défaut, confirmation au niveau FACILE (ancien comportement)
        return addConfirmationQuestion(session, competence, NiveauDifficulte.FACILE, nextOrdre);
    }
    
    /**
     * Récupérer questions filtrées par compétence et pondération
     */
    private List<Question> getQuestionsByCompetenceAndDifficulte(
        Competence competence, 
        NiveauDifficulte difficulte
    ) {
        return questionRepository.findAll().stream()
            .filter(q -> 
                q.getCompetences() != null && 
                q.getCompetences().contains(competence) &&
                q.getDifficulte() == difficulte
            )
            .collect(Collectors.toList());
    }
    
    /**
     * Sélectionner une question aléatoire
     */
    private Question selectRandomQuestion(List<Question> questions) {
        int randomIdx = new Random().nextInt(questions.size());
        return questions.get(randomIdx);
    }
    
    /**
     * Créer un objet QuestionSession (non sauvegardé)
     */
    private QuestionSession createQuestionSession(
        SessionTest session, 
        Question question, 
        int ordre, 
        TypeQSession type
    ) {
        return QuestionSession.builder()
            .session(session)
            .question(question)
            .ordre(ordre)
            .type(type)
            .estRepondue(false)
            .estCorrecte(null)  // Pas encore répondue
            .confirmationFailed(false)  // ← Ajouté: initialiser le flag
            .build();
    }
    
    /**
     * Compter le nombre total de questions sélectionnées
     * (pour validation)
     */
    public int countTotalQuestions(SessionTest session) {
        return (int) session.getQuestionSessions().stream()
            .filter(qs -> qs.getType() == TypeQSession.NORMALE)
            .count();
    }
    
    /**
     * Compter les questions de confirmation
     */
    public int countConfirmationQuestions(SessionTest session) {
        return (int) session.getQuestionSessions().stream()
            .filter(qs -> qs.getType() == TypeQSession.CONFIRMATION)
            .count();
    }
}

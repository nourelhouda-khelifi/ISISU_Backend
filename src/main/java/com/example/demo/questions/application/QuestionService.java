package com.example.demo.questions.application;

import com.example.demo.questions.domain.Choix;
import com.example.demo.questions.domain.Question;
import com.example.demo.questions.domain.enums.NiveauDifficulte;
import com.example.demo.questions.domain.enums.TypeQuestion;
import com.example.demo.questions.repository.ChoixRepository;
import com.example.demo.questions.repository.QuestionRepository;
import com.example.demo.referentiel.domain.Competence;
import com.example.demo.referentiel.infrastructure.CompetenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service pour gérer la banque de questions.
 *
 * Responsabilités :
 *   - Créer/modifier/supprimer des questions
 *   - Récupérer les questions selon les critères (compétence, difficulté, type)
 *   - Valider la cohérence des questions et choix
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final ChoixRepository choixRepository;
    private final CompetenceRepository competenceRepository;

    /**
     * Créer une nouvelle question avec ses choix
     */
    public Question creerQuestion(
        String enonce,
        TypeQuestion type,
        NiveauDifficulte difficulte,
        Integer dureeSecondes,
        List<Long> competenceIds,
        List<Choix> choix
    ) {
        log.info("Création question: type={}, difficulte={}", type, difficulte);

        // Charger les compétences
        List<Competence> competences = competenceRepository.findAllById(competenceIds);
        if (competences.isEmpty()) {
            throw new IllegalArgumentException("Au moins une compétence obligatoire");
        }

        // Créer la question
        Question question = Question.builder()
            .enonce(enonce)
            .type(type)
            .difficulte(difficulte)
            .dureeSecondes(dureeSecondes)
            .actif(true)
            .competences(competences)
            .build();

        // Associer les choix
        for (Choix c : choix) {
            c.setQuestion(question);
        }
        question.setChoix(choix);

        return questionRepository.save(question);
    }

    /**
     * Récupérer une question avec tous ses choix
     */
    public Question getQuestionById(Long id) {
        return questionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Question non trouvée: " + id));
    }

    /**
     * Récupérer toutes les questions actives
     */
    public List<Question> getAllQuestionsActives() {
        return questionRepository.findByActifTrue();
    }

    /**
     * Récupérer TOUTES les questions (actives ET inactives)
     * Utilisé par l'admin pour gérer toutes les questions
     */
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    /**
     * Récupérer les questions d'une compétence donnée
     * (utilisé pour construire un test d'évaluation)
     */
    public List<Question> getQuestionsByCompetence(Long competenceId) {
        return questionRepository.findByCompetenceId(competenceId);
    }

    /**
     * Récupérer les questions pour plusieurs compétences (test complet)
     */
    public List<Question> getQuestionsByCompetences(List<Long> competenceIds) {
        if (competenceIds == null || competenceIds.isEmpty()) {
            throw new IllegalArgumentException("Au moins une compétence requise");
        }
        return questionRepository.findByCompetenceIds(competenceIds);
    }

    /**
     * Récupérer les questions filtrées par compétences + difficulté
     * (ex : questions DIFFICILE pour une compétence donnée)
     */
    public List<Question> getQuestionsByCompetencesAndDifficulte(
        List<Long> competenceIds,
        NiveauDifficulte difficulte
    ) {
        return questionRepository.findByCompetencesAndDifficulte(competenceIds, difficulte);
    }

    /**
     * Récupérer les questions par type (QCM_SIMPLE, VRAI_FAUX, etc.)
     */
    public List<Question> getQuestionsByType(TypeQuestion type) {
        return questionRepository.findByType(type);
    }

    /**
     * Récupérer les questions par difficulté
     */
    public List<Question> getQuestionsByDifficulte(NiveauDifficulte difficulte) {
        return questionRepository.findByDifficulte(difficulte);
    }

    /**
     * Récupérer les choix d'une question (ordonnés)
     */
    public List<Choix> getChoixByQuestion(Long questionId) {
        return choixRepository.findByQuestionIdOrderByOrdre(questionId);
    }

    /**
     * Vérifier si une réponse est correcte (pour TEXTE_TROU)
     * Comparaison case-insensitive
     */
    public boolean verifierReponseTexteTrou(Long questionId, String reponse) {
        List<Choix> corrects = choixRepository
            .findCorrectChoixByContenus(questionId, reponse.trim());
        return !corrects.isEmpty();
    }

    /**
     * Récupérer les choix corrects d'une question
     * (utile pour afficher les solutions)
     */
    public List<Choix> getChoixCorrects(Long questionId) {
        return choixRepository.findByQuestionIdAndEstCorrectTrue(questionId);
    }

    /**
     * Désactiver une question
     */
    public void desactiverQuestion(Long questionId) {
        Question q = getQuestionById(questionId);
        q.setActif(false);
        questionRepository.save(q);
        log.info("Question {} désactivée", questionId);
    }

    /**
     * Activer une question
     */
    public void activerQuestion(Long questionId) {
        Question q = getQuestionById(questionId);
        q.setActif(true);
        questionRepository.save(q);
        log.info("Question {} activée", questionId);
    }

    /**
     * Calculer la pondération d'une question selon sa difficulté
     * (FACILE=1.0, MOYEN=1.5, DIFFICILE=2.0)
     */
    public double getPonderationQuestion(Long questionId) {
        Question q = getQuestionById(questionId);
        return q.getDifficulte().getPonderation();
    }

    /**
     * Modifier une question existante (admin)
     */
    public Question modifierQuestion(
        Long questionId,
        String enonce,
        TypeQuestion type,
        NiveauDifficulte difficulte,
        Integer dureeSecondes,
        boolean actif,
        List<Long> competenceIds,
        List<Choix> choix
    ) {
        log.info("Modification question: id={}", questionId);

        Question q = getQuestionById(questionId);

        // Charger les compétences
        List<Competence> competences = competenceRepository.findAllById(competenceIds);
        if (competences.isEmpty()) {
            throw new IllegalArgumentException("Au moins une compétence obligatoire");
        }

        // Mettre à jour les champs
        q.setEnonce(enonce);
        q.setType(type);
        q.setDifficulte(difficulte);
        q.setDureeSecondes(dureeSecondes);
        q.setActif(actif);
        q.setCompetences(competences);

        // Supprimer les anciens choix et en ajouter de nouveaux
        q.getChoix().clear();
        for (Choix c : choix) {
            c.setQuestion(q);
            q.getChoix().add(c);
        }

        return questionRepository.save(q);
    }

    /**
     * Supprimer une question et ses choix (admin)
     */
    public void supprimerQuestion(Long questionId) {
        log.info("Suppression question: id={}", questionId);
        if (!questionRepository.existsById(questionId)) {
            throw new IllegalArgumentException("Question non trouvée: " + questionId);
        }
        questionRepository.deleteById(questionId);
    }
}

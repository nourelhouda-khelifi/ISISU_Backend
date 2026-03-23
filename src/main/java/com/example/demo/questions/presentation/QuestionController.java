package com.example.demo.questions.presentation;

import com.example.demo.questions.application.QuestionService;
import com.example.demo.questions.domain.Question;
import com.example.demo.questions.domain.enums.NiveauDifficulte;
import com.example.demo.questions.domain.enums.TypeQuestion;
import com.example.demo.questions.presentation.dto.ChoixDTO;
import com.example.demo.questions.presentation.dto.QuestionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * API REST pour la Banque de Questions (Module 3)
 *
 * Endpoints Publics (pas d'authentification requise) :
 *   - GET /api/v1/questions
 *   - GET /api/v1/questions/{id}
 *   - GET /api/v1/questions/competence/{competenceId}
 *   - GET /api/v1/questions/type/{type}
 *
 * À faire (avec authentification Admin) :
 *   - POST /api/v1/questions (créer)
 *   - PUT /api/v1/questions/{id} (modifier)
 *   - DELETE /api/v1/questions/{id} (supprimer)
 */
@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Slf4j
public class QuestionController {

    private final QuestionService questionService;

    /**
     * Récupérer toutes les questions actives
     * GET /api/v1/questions
     */
    @GetMapping
    public ResponseEntity<List<QuestionDTO>> getAllQuestions() {
        log.info("GET /questions");
        List<Question> questions = questionService.getAllQuestionsActives();
        List<QuestionDTO> dtos = questions.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Récupérer une question par ID
     * GET /api/v1/questions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable Long id) {
        log.info("GET /questions/{}", id);
        Question question = questionService.getQuestionById(id);
        return ResponseEntity.ok(toDTO(question));
    }

    /**
     * Récupérer les questions d'une compétence
     * GET /api/v1/questions/competence/{competenceId}
     */
    @GetMapping("/competence/{competenceId}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByCompetence(
        @PathVariable Long competenceId
    ) {
        log.info("GET /questions/competence/{}", competenceId);
        List<Question> questions = questionService.getQuestionsByCompetence(competenceId);
        List<QuestionDTO> dtos = questions.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Récupérer les questions d'un type donné
     * GET /api/v1/questions/type/{type}
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByType(
        @PathVariable TypeQuestion type
    ) {
        log.info("GET /questions/type/{}", type);
        List<Question> questions = questionService.getQuestionsByType(type);
        List<QuestionDTO> dtos = questions.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Récupérer les questions par difficulté
     * GET /api/v1/questions/difficulte/{difficulte}
     */
    @GetMapping("/difficulte/{difficulte}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByDifficulte(
        @PathVariable NiveauDifficulte difficulte
    ) {
        log.info("GET /questions/difficulte/{}", difficulte);
        List<Question> questions = questionService.getQuestionsByDifficulte(difficulte);
        List<QuestionDTO> dtos = questions.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Convertir une entité Question en DTO
     * (masquer estCorrect, réponses correctes)
     */
    private QuestionDTO toDTO(Question question) {
        List<ChoixDTO> choixDTOs = question.getChoix().stream()
            .map(choix -> new ChoixDTO(
                choix.getId(),
                choix.getContenu(),
                choix.getOrdre()
                // estCorrect intentionnellement absent
            ))
            .collect(Collectors.toList());

        List<Long> competenceIds = question.getCompetences().stream()
            .map(com.example.demo.referentiel.domain.Competence::getId)
            .collect(Collectors.toList());

        return QuestionDTO.builder()
            .id(question.getId())
            .enonce(question.getEnonce())
            .type(question.getType())
            .difficulte(question.getDifficulte())
            .ponderation(question.getDifficulte().getPonderation())
            .dureeSecondes(question.getDureeSecondes())
            .actif(question.isActif())
            .dateCreation(question.getDateCreation())
            .competenceIds(competenceIds)
            .choix(choixDTOs)
            .build();
    }
}

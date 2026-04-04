package com.example.demo.evaluation.presentation;

import com.example.demo.auth.infrastructure.AuthenticationFacade;
import com.example.demo.auth.domain.Utilisateur;
import com.example.demo.evaluation.dto.*;
import com.example.demo.evaluation.service.SessionTestService;
import com.example.demo.recommendation.service.RecommendationService;
import com.example.demo.recommendation.dto.RecommendationData;
import com.example.demo.evaluation.domain.*;
import com.example.demo.evaluation.domain.enums.StatutSession;
import com.example.demo.questions.domain.Question;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour l'évaluation adaptative (Module 4 Phase 3)
 */
@RestController
@RequestMapping("/api/v1/eval")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Évaluation", description = "API REST pour l'évaluation adaptative")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasAnyRole('ETUDIANT', 'CANDIDAT_VAE', 'FORMATEUR', 'ADMIN')")
public class SessionTestController {

    private final SessionTestService sessionTestService;
    private final RecommendationService recommendationService;
    private final AuthenticationFacade authenticationFacade;

    /**
     * POST /api/v1/eval/sessions
     * Créer une nouvelle session
     */
    @PostMapping("/sessions")
    @Operation(summary = "Créer une nouvelle session")
    public ResponseEntity<SessionResponse> createSession() {
        try {
            Utilisateur user = authenticationFacade.getCurrentUser();
            log.info("Création session pour: {}", user.getEmail());
            
            SessionTest session = sessionTestService.createNewSession(user);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(
                SessionResponse.builder()
                    .sessionId(session.getId())
                    .statut(session.getStatut())
                    .dateDebut(session.getDateDebut())
                    .tempsRestantSecondes(getTimeRemaining(session))
                    .totalQuestions(session.getQuestionSessions() != null ? session.getQuestionSessions().size() : 0)
                    .message("Session créée avec succès")
                    .build()
            );
        } catch (Exception e) {
            log.error("Erreur création session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/v1/eval/sessions/current
     * Récupérer la session en cours
     */
    @GetMapping("/sessions/current")
    @Operation(summary = "Récupérer la session en cours")
    public ResponseEntity<SessionStatusDTO> getCurrentSession() {
        try {
            Utilisateur user = authenticationFacade.getCurrentUser();
            var sessionOpt = sessionTestService.getCurrentSession(user);
            
            if (!sessionOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            SessionTest session = sessionOpt.get();
            long total = session.getQuestionSessions() != null ? session.getQuestionSessions().size() : 0;
            long answered = session.getQuestionSessions() != null ? 
                session.getQuestionSessions().stream()
                    .filter(q -> q.getEstRepondue() != null && q.getEstRepondue()).count() : 0;
            
            return ResponseEntity.ok(
                SessionStatusDTO.builder()
                    .sessionId(session.getId())
                    .statut(session.getStatut().toString())
                    .dateDebut(session.getDateDebut())
                    .tempsRestantSecondes(getTimeRemaining(session))
                    .questionsRepondues((int) answered)
                    .totalQuestions((int) total)
                    .pourcentageAvancement(total > 0 ? (answered * 100.0 / total) : 0)
                    .currentModuleCode("E3-1-IN-1")
                    .currentModuleNom("Évaluation en cours")
                    .build()
            );
        } catch (Exception e) {
            log.error("Erreur récupération session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/v1/eval/sessions/current/question
     * Récupérer la prochaine question
     */
    @GetMapping("/sessions/current/question")
    @Operation(summary = "Récupérer la prochaine question")
    public ResponseEntity<QuestionDisplayDTO> getNextQuestion() {
        try {
            Utilisateur user = authenticationFacade.getCurrentUser();
            var sessionOpt = sessionTestService.getCurrentSession(user);
            
            if (!sessionOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            var questionOpt = sessionTestService.getNextQuestion(sessionOpt.get());
            if (!questionOpt.isPresent()) {
                return ResponseEntity.noContent().build();
            }
            
            QuestionSession qs = questionOpt.get();
            Question q = qs.getQuestion();
            
            return ResponseEntity.ok(
                QuestionDisplayDTO.builder()
                    .questionSessionId(qs.getId())
                    .questionId(q.getId())
                    .enonce(q.getEnonce())
                    .type(q.getType())
                    .niveauDifficulte(q.getDifficulte().toString())
                    .dureeRecommandeeSecondes(q.getDureeSecondes() != null ? q.getDureeSecondes() : 30)
                    .ordre(String.valueOf(qs.getOrdre()))
                    .statut(qs.getEstRepondue() ? "REPONDUE" : "NON_REPONDUE")
                    .choix(q.getChoix() != null ? q.getChoix().stream()
                        .map(c -> ChoixDTO.builder()
                                .id(c.getId())
                                .libelle(c.getContenu())
                                .build())
                        .toList() : null)
                    .build()
            );
        } catch (Exception e) {
            log.error("Erreur récupération question", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /api/v1/eval/sessions/current/answer
     * Soumettre une réponse
     */
    @PostMapping("/sessions/current/answer")
    @Operation(summary = "Soumettre une réponse")
    public ResponseEntity<AnswerResponseDTO> submitAnswer(@RequestBody SubmitAnswerRequest request) {
        try {
            Utilisateur user = authenticationFacade.getCurrentUser();
            var sessionOpt = sessionTestService.getCurrentSession(user);
            
            if (!sessionOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            SessionTest session = sessionOpt.get();
            
            // Créer/remplir la réponse
            ReponseEtudiant response = new ReponseEtudiant();
            response.setReponseTexte(request.getReponseTexte());
            response.setDateReponse(java.time.LocalDateTime.now());
            
            // Sérialiser les choix en JSON si présents
            if (request.getChoixIds() != null && !request.getChoixIds().isEmpty()) {
                response.setChoixSelectionnesJSON(request.getChoixIds().toString());
            }
            
            // Trouver la question
            QuestionSession qs = session.getQuestionSessions().stream()
                .filter(q -> q.getId().equals(request.getQuestionSessionId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Question not found"));
            
            // Soumettre
            sessionTestService.submitAnswer(session, qs, response);
            
            // RE-FETCH session pour avoir l'état à jour (questions dynamically added)
            session = sessionTestService.getCurrentSession(user).orElse(session);
            
            // Récupérer la prochaine question
            var nextQOpt = sessionTestService.getNextQuestion(session);
            
            QuestionDisplayDTO nextQ = null;
            if (nextQOpt.isPresent()) {
                QuestionSession nqs = nextQOpt.get();
                Question q = nqs.getQuestion();
                nextQ = QuestionDisplayDTO.builder()
                    .questionSessionId(nqs.getId())
                    .questionId(q.getId())
                    .enonce(q.getEnonce())
                    .type(q.getType())
                    .niveauDifficulte(q.getDifficulte().toString())
                    .dureeRecommandeeSecondes(q.getDureeSecondes() != null ? q.getDureeSecondes() : 30)
                    .ordre(String.valueOf(nqs.getOrdre()))
                    .statut(nqs.getEstRepondue() ? "REPONDUE" : "NON_REPONDUE")
                    .choix(q.getChoix() != null ? q.getChoix().stream()
                        .map(c -> ChoixDTO.builder()
                                .id(c.getId())
                                .libelle(c.getContenu())
                                .build())
                        .toList() : null)
                    .build();
            }
            
            return ResponseEntity.ok(
                AnswerResponseDTO.builder()
                    .correct(response.getEstCorrecte())
                    .feedback("Réponse enregistrée")
                    .nextQuestion(nextQ)
                    .sessionTerminated(nextQ == null)
                    .tempsRestantSecondes((long) getTimeRemaining(session))
                    .build()
            );
        } catch (Exception e) {
            log.error("Erreur soumission réponse", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /api/v1/eval/sessions/current/terminate
     * Terminer la session
     */
    @PostMapping("/sessions/current/terminate")
    @Operation(summary = "Terminer la session")
    public ResponseEntity<SessionResultsDTO> terminateSession() {
        try {
            Utilisateur user = authenticationFacade.getCurrentUser();
            var sessionOpt = sessionTestService.getCurrentSession(user);
            
            if (!sessionOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            SessionTest session = sessionOpt.get();
            sessionTestService.terminateSession(session, StatutSession.TERMINEE, "Fin normale");
            
            long total = session.getReponses() != null ? session.getReponses().size() : 0;
            long correct = session.getReponses() != null ? 
                session.getReponses().stream().filter(r -> r.getEstCorrecte() != null && r.getEstCorrecte()).count() : 0;
            
            return ResponseEntity.ok(
                SessionResultsDTO.builder()
                    .sessionId(session.getId())
                    .dateDebut(session.getDateDebut())
                    .dateFin(session.getDateFin())
                    .dureeMinutes(calculateDuration(session))
                    .totalQuestionsRepondues((int) total)
                    .totalCorrect((int) correct)
                    .scoreGlobal(total > 0 ? (correct * 100.0 / total) : 0)
                    .competences(session.getScores() != null ? 
                        session.getScores().stream().map(sc -> 
                            CompetenceScoreDTO.builder()
                                .competenceId(sc.getCompetence().getId())
                                .intitule(sc.getCompetence().getIntitule())
                                .codeModule(sc.getCompetence().getModule().getCode())
                                .statut(sc.getStatut())
                                .scoreObtenu(sc.getScoreObtenu())
                                .niveauAtteint(sc.getNiveauAtteint().toString())
                                .confirmationLacune(false) // Pas de confirmation dans ScoreCompetence
                                .scoreSession_precedente(sc.getScorePrecedent())
                                .evolution(sc.getEvolutionPourcentage() != null ? 
                                    (sc.getEvolutionPourcentage() > 0 ? "PROGRESSION" : "REGRESSION") : "STABLE")
                                .build()
                        ).toList() : List.of())
                    .build()
            );
        } catch (Exception e) {
            log.error("Erreur terminaison session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/v1/eval/sessions
     * Obtenir l'historique des sessions
     */
    @GetMapping("/sessions")
    @Operation(summary = "Obtenir l'historique")
    public ResponseEntity<List<SessionHistoryDTO>> getHistory() {
        try {
            Utilisateur user = authenticationFacade.getCurrentUser();
            List<SessionTest> sessions = sessionTestService.getUserSessions(user);
            
            return ResponseEntity.ok(
                sessions.stream().map(s -> 
                    SessionHistoryDTO.builder()
                        .sessionId(s.getId())
                        .dateDebut(s.getDateDebut())
                        .dateFin(s.getDateFin())
                        .statut(s.getStatut().toString())
                        .dureeMinutes(calculateDuration(s))
                        .scoreGlobal(s.getScores() != null ? 
                            s.getScores().stream().mapToDouble(sc -> sc.getScoreObtenu()).average().orElse(0) : 0)
                        .lacunesDetectees(0) // Simplification MVP
                        .build()
                ).toList()
            );
        } catch (Exception e) {
            log.error("Erreur récupération historique", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/v1/eval/sessions/{id}/recommendations
     * Récupérer les recommandations d'une session (PHASE 1: Algo manuel)
     */
    @GetMapping("/sessions/{id}/recommendations")
    @Operation(
        summary = "Récupérer les recommandations pour une session",
        description = "PHASE 1: Génère les recommandations algorithmiques basées sur les scores de la session"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Recommandations générées avec succès",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RecommendationData.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Session non trouvée"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Accès refusé - vous n'êtes pas propriétaire de cette session"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Erreur serveur lors du calcul des recommandations"
        )
    })
    public ResponseEntity<RecommendationData> getRecommendations(
        @io.swagger.v3.oas.annotations.Parameter(
            name = "id",
            description = "ID de la session d'évaluation",
            required = true,
            example = "123"
        )
        @PathVariable Long id
    ) {
        try {
            // Get session
            SessionTest session = sessionTestService.getSessionById(id);
            if (session == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Verify ownership if user is authenticated (user owns the session or is admin)
            try {
                Utilisateur user = authenticationFacade.getCurrentUser();
                if (!session.getUtilisateur().getId().equals(user.getId()) && 
                    !user.getRole().name().equals("ADMIN")) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            } catch (RuntimeException e) {
                // Utilisateur non authentifié - on laisse passer (endpoint public pour tests)
                log.info("Endpoint public - pas d'authentification requise");
            }
            
            // Compute recommendations (PHASE 1: algorithmic only)
            RecommendationData recommendations = recommendationService.computeStructuredData(session);
            
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            log.error("Erreur récupération recommandations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/v1/eval/sessions/{id}/recommendations-llm
     * Récupérer les recommandations enrichies par le LLM (PHASE 1 + PHASE 2)
     */
    @GetMapping("/sessions/{id}/recommendations-llm")
    @Operation(
        summary = "Récupérer les recommandations enrichies par le LLM",
        description = """
            PHASE 2: Enrichit les recommandations PHASE 1 avec une analyse personnalisée par Gemini.
            
            Contient:
            - phaseStructuree: données brutes calculées par l'algo (scores, progression, dépendances)
            - analyseLLM: analyse enrichie par le LLM (messages personnalisés, priorités, conseils)
            """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Recommandations LLM générées avec succès"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Session non trouvée"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Accès refusé - vous n'êtes pas propriétaire de cette session"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Erreur serveur lors de l'appel à l'API Gemini ou du calcul"
        )
    })
    public ResponseEntity<?> getRecommendationsWithLLM(
        @io.swagger.v3.oas.annotations.Parameter(
            name = "id",
            description = "ID de la session d'évaluation",
            required = true,
            example = "123"
        )
        @PathVariable Long id
    ) {
        try {
            // Get session
            SessionTest session = sessionTestService.getSessionById(id);
            if (session == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Verify ownership if user is authenticated
            try {
                Utilisateur user = authenticationFacade.getCurrentUser();
                if (!session.getUtilisateur().getId().equals(user.getId()) && 
                    !user.getRole().name().equals("ADMIN")) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            } catch (RuntimeException e) {
                log.info("Endpoint public pour LLM - pas d'authentification requise");
            }
            
            log.info("PHASE 2 - Enrichissement LLM pour session {}", id);
            
            // PHASE 1: Compute structured data
            RecommendationData structuredData = recommendationService.computeStructuredData(session);
            
            // PHASE 2: Enrich with LLM
            var result = recommendationService.enrichWithLLM(structuredData);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Erreur enrichissement LLM", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Erreur lors de l'appel LLM",
                    "message", e.getMessage()
                ));
        }
    }

    // Helper methods
    private Integer getTimeRemaining(SessionTest session) {
        if (session.getDateDebut() == null) return 0;
        long elapsed = System.currentTimeMillis() - 
            session.getDateDebut().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        long remaining = (1800000 - elapsed) / 1000; // 30 min
        return Math.max(0, (int) remaining);
    }

    private Integer calculateDuration(SessionTest session) {
        if (session.getDateFin() == null) return 0;
        long start = session.getDateDebut().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        long end = session.getDateFin().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        return (int) ((end - start) / 60000);
    }
}

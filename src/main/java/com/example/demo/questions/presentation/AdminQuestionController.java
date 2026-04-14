package com.example.demo.questions.presentation;

import com.example.demo.questions.application.QuestionService;
import com.example.demo.questions.domain.Choix;
import com.example.demo.questions.domain.Question;
import com.example.demo.questions.domain.enums.NiveauDifficulte;
import com.example.demo.questions.domain.enums.TypeQuestion;
import com.example.demo.questions.presentation.dto.AdminChoixDTO;
import com.example.demo.questions.presentation.dto.AdminQuestionDTO;
import com.example.demo.questions.presentation.dto.ChoixDTO;
import com.example.demo.questions.presentation.dto.CreateChoixDTO;
import com.example.demo.questions.presentation.dto.CreateQuestionDTO;
import com.example.demo.questions.presentation.dto.QuestionDTO;
import com.example.demo.questions.presentation.dto.UpdateQuestionDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * API REST Admin — Gestion de la Banque de Questions
 *
 * ⚠️ Toutes les routes sont PROTÉGÉES (authentification requise)
 * Rôle requis : ADMIN
 *
 * GET Endpoints (Lecture - questions ACTIVES + INACTIVES):
 *   - GET  /api/v1/admin/questions              → récupérer toutes les questions
 *   - GET  /api/v1/admin/questions/{id}         → récupérer une question par ID
 *   - GET  /api/v1/admin/questions?type=...     → filtrer par type
 *   - GET  /api/v1/admin/questions?difficulte=...  → filtrer par difficulté
 *   - GET  /api/v1/admin/questions?actif=true/false  → filtrer par état
 *
 * POST/PUT/DELETE Endpoints (Écriture):
 *   - POST   /api/v1/admin/questions              → créer une question
 *   - PUT    /api/v1/admin/questions/{id}         → modifier une question
 *   - DELETE /api/v1/admin/questions/{id}         → supprimer une question
 *   - POST   /api/v1/admin/questions/{id}/activer  → activer
 *   - POST   /api/v1/admin/questions/{id}/desactiver → désactiver
 */
@RestController
@RequestMapping("/api/v1/admin/questions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Questions", description = "Endpoints for admin question management")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminQuestionController {

    private final QuestionService questionService;

    /**
     * Récupérer TOUTES les questions (actives ET inactives) avec filtres optionnels
     * GET /api/v1/admin/questions
     * GET /api/v1/admin/questions?type=QCM_SIMPLE
     * GET /api/v1/admin/questions?difficulte=DIFFICILE
     * GET /api/v1/admin/questions?actif=false
     * 
     * ✅ RETOURNE AdminQuestionDTO (inclut estCorrect)
     */
    @GetMapping
    public ResponseEntity<List<AdminQuestionDTO>> getAllQuestions(
        @RequestParam(required = false) TypeQuestion type,
        @RequestParam(required = false) NiveauDifficulte difficulte,
        @RequestParam(required = false) Boolean actif
    ) {
        log.info("GET /admin/questions - type: {}, difficulte: {}, actif: {}", type, difficulte, actif);
        
        // Récupérer TOUTES les questions (sans filtre actif)
        List<Question> questions = questionService.getAllQuestions();
        
        // Filtrer selon les paramètres optionnels
        if (type != null) {
            questions = questions.stream()
                .filter(q -> q.getType() == type)
                .collect(Collectors.toList());
        }
        
        if (difficulte != null) {
            questions = questions.stream()
                .filter(q -> q.getDifficulte() == difficulte)
                .collect(Collectors.toList());
        }
        
        if (actif != null) {
            questions = questions.stream()
                .filter(q -> q.isActif() == actif)
                .collect(Collectors.toList());
        }
        
        List<AdminQuestionDTO> dtos = questions.stream()
            .map(this::toAdminDTO)  // ✅ Utilise toAdminDTO (avec estCorrect)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * Récupérer une question spécifique (admin)
     * GET /api/v1/admin/questions/{id}
     * 
     * ✅ RETOURNE AdminQuestionDTO (inclut estCorrect)
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminQuestionDTO> getQuestionById(@PathVariable Long id) {
        log.info("GET /admin/questions/{}", id);
        Question question = questionService.getQuestionById(id);
        return ResponseEntity.ok(toAdminDTO(question));  // ✅ Utilise toAdminDTO
    }

    /**
     * Créer une nouvelle question (admin)
     * POST /api/v1/admin/questions
     *
     * Exemple request :
     * {
     *   "enonce": "Quelle est la capitale du France ?",
     *   "type": "QCM_SIMPLE",
     *   "difficulte": "FACILE",
     *   "dureeSecondes": 30,
     *   "competenceIds": [1, 2],
     *   "choix": [
     *     {"contenu": "Paris", "estCorrect": true, "ordre": 1},
     *     {"contenu": "Lyon", "estCorrect": false, "ordre": 2}
     *   ]
     * }
     */
    @PostMapping
    public ResponseEntity<QuestionDTO> creerQuestion(
        @RequestBody CreateQuestionDTO dto
    ) {
        log.info("POST /admin/questions - créer: type={}, difficulte={}", 
            dto.getType(), dto.getDifficulte());

        // Convertir DTOs en entités Choix
        List<Choix> choix = dto.getChoix().stream()
            .map(c -> Choix.builder()
                .contenu(c.getContenu())
                .estCorrect(c.isEstCorrect())
                .ordre(c.getOrdre())
                .build())
            .collect(Collectors.toList());

        // Créer la question
        Question question = questionService.creerQuestion(
            dto.getEnonce(),
            dto.getType(),
            dto.getDifficulte(),
            dto.getDureeSecondes(),
            dto.getCompetenceIds(),
            choix
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(question));
    }

    /**
     * Modifier une question existante (admin)
     * PUT /api/v1/admin/questions/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<QuestionDTO> modifierQuestion(
        @PathVariable Long id,
        @RequestBody UpdateQuestionDTO dto
    ) {
        log.info("PUT /admin/questions/{} - modifier", id);

        // Convertir DTOs en entités Choix
        List<Choix> choix = dto.getChoix().stream()
            .map(c -> Choix.builder()
                .contenu(c.getContenu())
                .estCorrect(c.isEstCorrect())
                .ordre(c.getOrdre())
                .build())
            .collect(Collectors.toList());

        // Modifier la question
        Question question = questionService.modifierQuestion(
            id,
            dto.getEnonce(),
            dto.getType(),
            dto.getDifficulte(),
            dto.getDureeSecondes(),
            dto.isActif(),
            dto.getCompetenceIds(),
            choix
        );

        return ResponseEntity.ok(toDTO(question));
    }

    /**
     * Supprimer une question (admin)
     * DELETE /api/v1/admin/questions/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerQuestion(@PathVariable Long id) {
        log.info("DELETE /admin/questions/{}", id);
        questionService.supprimerQuestion(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Activer une question (admin)
     * POST /api/v1/admin/questions/{id}/activer
     */
    @PostMapping("/{id}/activer")
    public ResponseEntity<QuestionDTO> activerQuestion(@PathVariable Long id) {
        log.info("POST /admin/questions/{}/activer", id);
        questionService.activerQuestion(id);
        Question question = questionService.getQuestionById(id);
        return ResponseEntity.ok(toDTO(question));
    }

    /**
     * Désactiver une question (admin)
     * POST /api/v1/admin/questions/{id}/desactiver
     */
    @PostMapping("/{id}/desactiver")
    public ResponseEntity<QuestionDTO> desactiverQuestion(@PathVariable Long id) {
        log.info("POST /admin/questions/{}/desactiver", id);
        questionService.desactiverQuestion(id);
        Question question = questionService.getQuestionById(id);
        return ResponseEntity.ok(toDTO(question));
    }

    /**
     * Convertir une entité Question en DTO (pour retour vers student/user)
     * (masquer estCorrect, réponses correctes)
     * 
     * Utilisé par: POST, PUT, DELETE endpoints (jamais retourné à l'admin pour lecture)
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

    /**
     * Convertir une entité Question en AdminQuestionDTO (pour admin)
     * ✅ INCLUT estCorrect dans les choix
     * 
     * Utilisé par: GET endpoints uniquement (pour affichage admin)
     * Sécurité: Ne jamais retourner à un utilisateur normal
     */
    private AdminQuestionDTO toAdminDTO(Question question) {
        List<AdminChoixDTO> adminChoixDTOs = question.getChoix().stream()
            .map(choix -> AdminChoixDTO.builder()
                .id(choix.getId())
                .contenu(choix.getContenu())
                .ordre(choix.getOrdre())
                .estCorrect(choix.isEstCorrect())  // ✅ INCLUS pour admin
                .build())
            .collect(Collectors.toList());

        List<Long> competenceIds = question.getCompetences().stream()
            .map(com.example.demo.referentiel.domain.Competence::getId)
            .collect(Collectors.toList());

        return AdminQuestionDTO.builder()
            .id(question.getId())
            .enonce(question.getEnonce())
            .type(question.getType())
            .difficulte(question.getDifficulte())
            .ponderation(question.getDifficulte().getPonderation())
            .dureeSecondes(question.getDureeSecondes())
            .actif(question.isActif())
            .dateCreation(question.getDateCreation())
            .competenceIds(competenceIds)
            .choix(adminChoixDTOs)  // ✅ AdminChoixDTO avec estCorrect
            .build();
    }
}

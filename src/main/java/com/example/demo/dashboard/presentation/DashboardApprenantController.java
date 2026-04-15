package com.example.demo.dashboard.presentation;

import com.example.demo.auth.domain.Utilisateur;
import com.example.demo.auth.infrastructure.repository.UtilisateurRepository;
import com.example.demo.auth.infrastructure.AuthenticationFacade;
import com.example.demo.common.response.ApiResponse;
import com.example.demo.dashboard.dto.*;
import com.example.demo.dashboard.service.DashboardApprenantService;
import com.example.demo.evaluation.domain.SessionTest;
import com.example.demo.referentiel.domain.enums.Semestre;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller pour le dashboard apprenant
 */
@RestController
@RequestMapping("/api/v1/dashboard/apprenant")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dashboard Apprenant", description = "API du dashboard apprenant")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasAnyRole('ETUDIANT_FIE3', 'CANDIDAT_VAE')")
public class DashboardApprenantController {
    
    private final DashboardApprenantService dashboardApprenantService;
    private final AuthenticationFacade authenticationFacade;
    private final UtilisateurRepository utilisateurRepository;
    
    /**
     * GET /api/v1/dashboard/apprenant
     * Récupérer le dashboard complet
     */
    @GetMapping
    @Operation(summary = "Vue d'ensemble du dashboard apprenant")
    public ResponseEntity<ApiResponse<ApprenantDashboardDTO>> getDashboard() {
        log.debug("GET /dashboard/apprenant");
        Utilisateur user = authenticationFacade.getCurrentUser();
        ApprenantDashboardDTO dto = dashboardApprenantService.getApprenantDashboard(user);
        return ResponseEntity.ok(ApiResponse.success(200, "Dashboard récupéré", dto));
    }
    
    /**
     * GET /api/v1/dashboard/apprenant/sessions
     * Récupérer l'historique des sessions
     */
    @GetMapping("/sessions")
    @Operation(summary = "Historique des sessions")
    public ResponseEntity<ApiResponse<List<SessionTest>>> getSessions() {
        log.debug("GET /sessions");
        Utilisateur user = authenticationFacade.getCurrentUser();
        List<SessionTest> sessions = dashboardApprenantService.getSessions(user);
        return ResponseEntity.ok(ApiResponse.success(200, "Sessions récupérées", sessions));
    }
    
    /**
     * GET /api/v1/dashboard/apprenant/sessions/{sessionId}
     * Récupérer le détail d'une session
     */
    @GetMapping("/sessions/{sessionId}")
    @Operation(summary = "Détail d'une session")
    public ResponseEntity<ApiResponse<SessionDetailDTO>> getSessionDetail(@PathVariable Long sessionId) {
        log.debug("GET /sessions/{}", sessionId);
        Utilisateur user = authenticationFacade.getCurrentUser();
        SessionDetailDTO dto = dashboardApprenantService.getSessionDetail(sessionId, user);
        return ResponseEntity.ok(ApiResponse.success(200, "Détail de session", dto));
    }
    
    /**
     * GET /api/v1/dashboard/apprenant/competences
     * Récupérer la progression des compétences
     */
    @GetMapping("/competences")
    @Operation(summary = "Progression des compétences")
    public ResponseEntity<ApiResponse<List<CompetenceProgressDTO>>> getCompetences() {
        log.debug("GET /competences");
        Utilisateur user = authenticationFacade.getCurrentUser();
        List<CompetenceProgressDTO> competences = dashboardApprenantService.getCompetencesProgress(user);
        return ResponseEntity.ok(ApiResponse.success(200, "Compétences récupérées", competences));
    }
    
    /**
     * GET /api/v1/dashboard/apprenant/activite/7-jours
     * Récupérer l'activité des 7 derniers jours
     */
    @GetMapping("/activite/7-jours")
    @Operation(summary = "Activité des 7 derniers jours")
    public ResponseEntity<ApiResponse<List<ActiviteDTO>>> getActiviteRecente() {
        log.debug("GET /activite/7-jours");
        Utilisateur user = authenticationFacade.getCurrentUser();
        List<ActiviteDTO> activite = dashboardApprenantService.getActiviteRecente(user, 7);
        return ResponseEntity.ok(ApiResponse.success(200, "Activité récupérée", activite));
    }
    
    /**
     * GET /api/v1/dashboard/apprenant/ue/all
     * Récupérer les scores par UE pour TOUS les semestres
     */
    @GetMapping("/ue/all")
    @Operation(summary = "Scores agrégés par UE (tous les semestres)")
    public ResponseEntity<ApiResponse<List<UEScoreDTO>>> getScoresToutesUE() {
        log.debug("GET /ue/all - Récupération des scores par UE (tous)");
        Utilisateur user = authenticationFacade.getCurrentUser();
        List<UEScoreDTO> scores = dashboardApprenantService.getScoresToutesUE(user);
        return ResponseEntity.ok(ApiResponse.success(200, "Scores UE récupérés", scores));
    }
    
    /**
     * GET /api/v1/dashboard/apprenant/ue/{semestre}
     * Récupérer les scores par UE pour un semestre spécifique
     *
     * @param semestre S5, S6, etc.
     */
    @GetMapping("/ue/{semestre}")
    @Operation(summary = "Scores agrégés par UE pour un semestre")
    public ResponseEntity<ApiResponse<List<UEScoreDTO>>> getScoresParUE(@PathVariable String semestre) {
        log.debug("GET /ue/{} - Récupération des scores par UE", semestre);
        Utilisateur user = authenticationFacade.getCurrentUser();
        Semestre sem = Semestre.valueOf("S" + semestre);
        List<UEScoreDTO> scores = dashboardApprenantService.getScoresParUE(user, sem);
        return ResponseEntity.ok(ApiResponse.success(200, "Scores UE récupérés", scores));
    }
}



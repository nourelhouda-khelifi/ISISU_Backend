package com.example.demo.dashboard.presentation;

import com.example.demo.auth.domain.enums.Role;
import com.example.demo.auth.domain.enums.StatutCompte;
import com.example.demo.common.response.ApiResponse;
import com.example.demo.dashboard.dto.*;
import com.example.demo.dashboard.service.DashboardAdminService;
import com.example.demo.dashboard.service.StatistiquesService;
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
 * Controller pour le dashboard administrateur
 */
@RestController
@RequestMapping("/api/v1/dashboard/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dashboard Admin", description = "API du dashboard administrateur")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardAdminController {
    
    private final DashboardAdminService dashboardAdminService;
    private final StatistiquesService statistiquesService;
    
    /**
     * GET /api/v1/dashboard/admin
     * Récupérer la vue d'ensemble du dashboard
     */
    @GetMapping
    @Operation(summary = "Vue d'ensemble du dashboard admin")
    public ResponseEntity<ApiResponse<AdminDashboardDTO>> getDashboard() {
        log.debug("GET /dashboard/admin");
        AdminDashboardDTO dto = dashboardAdminService.getAdminDashboard();
        return ResponseEntity.ok(ApiResponse.success(200, "Dashboard récupéré", dto));
    }
    
    /**
     * GET /api/v1/dashboard/admin/users
     * Récupérer la liste des utilisateurs
     */
    @GetMapping("/users")
    @Operation(summary = "Lister les utilisateurs avec filtres")
    public ResponseEntity<ApiResponse<List<UtilisateurDashboardDTO>>> getUtilisateurs(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) StatutCompte statut) {
        log.debug("GET /users - role: {}, statut: {}", role, statut);
        List<UtilisateurDashboardDTO> users = dashboardAdminService.getUtilisateurs(role, statut);
        return ResponseEntity.ok(ApiResponse.success(200, "Liste des utilisateurs", users));
    }
    
    /**
     * GET /api/v1/dashboard/admin/utilisateurs/{idEtudiant}/historique
     * Récupérer l'historique complet d'un étudiant (sessions + scores)
     */
    @GetMapping("/utilisateurs/{idEtudiant}/historique")
    @Operation(summary = "Historique complet d'un étudiant (sessions et résultats)")
    public ResponseEntity<ApiResponse<AdminHistoriqueEtudiantDTO>> getHistoriqueEtudiant(
            @PathVariable Long idEtudiant) {
        log.debug("GET /utilisateurs/{}/historique", idEtudiant);
        AdminHistoriqueEtudiantDTO historique = dashboardAdminService.getHistoriqueEtudiant(idEtudiant);
        return ResponseEntity.ok(ApiResponse.success(200, "Historique de l'étudiant récupéré", historique));
    }
    
    /**
     * GET /api/v1/dashboard/admin/utilisateurs/{idEtudiant}/ue/all
     * Récupérer les scores par UE pour un étudiant (tous les semestres)
     */
    @GetMapping("/utilisateurs/{idEtudiant}/ue/all")
    @Operation(summary = "Scores agrégés par UE pour un étudiant (tous les semestres)")
    public ResponseEntity<ApiResponse<List<UEScoreDTO>>> getScoresUEToutsSemestresEtudiant(
            @PathVariable Long idEtudiant) {
        log.debug("GET /utilisateurs/{}/ue/all", idEtudiant);
        List<UEScoreDTO> scores = dashboardAdminService.getScoresUEToutsSemestresPourEtudiant(idEtudiant);
        return ResponseEntity.ok(ApiResponse.success(200, "Scores UE récupérés pour l'étudiant", scores));
    }
    
    /**
     * GET /api/v1/dashboard/admin/utilisateurs/{idEtudiant}/ue/{semestre}
     * Récupérer les scores par UE pour un étudiant et un semestre spécifique
     */
    @GetMapping("/utilisateurs/{idEtudiant}/ue/{semestre}")
    @Operation(summary = "Scores agrégés par UE pour un étudiant (semestre spécifique)")
    public ResponseEntity<ApiResponse<List<UEScoreDTO>>> getScoresUEEtudiant(
            @PathVariable Long idEtudiant,
            @PathVariable String semestre) {
        log.debug("GET /utilisateurs/{}/ue/{}", idEtudiant, semestre);
        Semestre sem = Semestre.valueOf("S" + semestre);
        List<UEScoreDTO> scores = dashboardAdminService.getScoresUEPourEtudiant(idEtudiant, sem);
        return ResponseEntity.ok(ApiResponse.success(200, "Scores UE récupérés pour l'étudiant", scores));
    }
    
    /**
     * GET /api/v1/dashboard/admin/statistiques/sessions
     * Récupérer les statistiques des sessions
     */
    @GetMapping("/statistiques/sessions")
    @Operation(summary = "Statistiques des sessions")
    public ResponseEntity<ApiResponse<SessionStatisticsDTO>> getSessionStatistics() {
        log.debug("GET /statistiques/sessions");
        SessionStatisticsDTO stats = dashboardAdminService.getSessionStatistics();
        return ResponseEntity.ok(ApiResponse.success(200, "Statistiques récupérées", stats));
    }
    
    /**
     * GET /api/v1/dashboard/admin/statistiques/competences
     * Récupérer les statistiques des compétences
     */
    @GetMapping("/statistiques/competences")
    @Operation(summary = "Statistiques des compétences (top et tendances)")
    public ResponseEntity<ApiResponse<StatistiquesCompetencesDTO>> getStatistiquesCompetences() {
        log.debug("GET /statistiques/competences");
        StatistiquesCompetencesDTO stats = statistiquesService.getStatistiquesCompetences();
        return ResponseEntity.ok(ApiResponse.success(200, "Statistiques des compétences récupérées", stats));
    }
    
    /**
     * GET /api/v1/dashboard/admin/statistiques/questions
     * Récupérer les statistiques des questions par type et difficulté
     */
    @GetMapping("/statistiques/questions")
    @Operation(summary = "Statistiques des questions (par type et difficulté)")
    public ResponseEntity<ApiResponse<StatistiquesQuestionsDTO>> getStatistiquesQuestions() {
        log.debug("GET /statistiques/questions");
        StatistiquesQuestionsDTO stats = statistiquesService.getStatistiquesQuestions();
        return ResponseEntity.ok(ApiResponse.success(200, "Statistiques des questions récupérées", stats));
    }
    
    /**
     * GET /api/v1/dashboard/admin/statistiques/performances
     * Récupérer les statistiques de performance des apprenants
     */
    @GetMapping("/statistiques/performances")
    @Operation(summary = "Statistiques des performances (distribution scores et par type d'apprenant)")
    public ResponseEntity<ApiResponse<StatistiquesPerformancesDTO>> getStatistiquesPerformances() {
        log.debug("GET /statistiques/performances");
        StatistiquesPerformancesDTO stats = statistiquesService.getStatistiquesPerformances();
        return ResponseEntity.ok(ApiResponse.success(200, "Statistiques des performances récupérées", stats));
    }
    
    /**
     * GET /api/v1/dashboard/admin/statistiques/activite
     * Récupérer les statistiques d'activité sur une période donnée
     */
    @GetMapping("/statistiques/activite")
    @Operation(summary = "Statistiques d'activité (sessions et apprenants actifs)")
    public ResponseEntity<ApiResponse<StatistiquesActiviteDTO>> getStatistiquesActivite(
            @RequestParam(defaultValue = "7j") String periode) {
        log.debug("GET /statistiques/activite - période: {}", periode);
        StatistiquesActiviteDTO stats = statistiquesService.getStatistiquesActivite(periode);
        return ResponseEntity.ok(ApiResponse.success(200, "Statistiques d'activité récupérées", stats));
    }
    
    /**
     * GET /api/v1/dashboard/admin/statistiques/heatmap
     * Récupérer la heatmap des compétences par difficulté
     */
    @GetMapping("/statistiques/heatmap")
    @Operation(summary = "Heatmap des compétences par niveau de difficulté")
    public ResponseEntity<ApiResponse<StatistiquesHeatmapDTO>> getStatistiquesHeatmap() {
        log.debug("GET /statistiques/heatmap");
        StatistiquesHeatmapDTO stats = statistiquesService.getStatistiquesHeatmap();
        return ResponseEntity.ok(ApiResponse.success(200, "Heatmap des statistiques récupérée", stats));
    }
}

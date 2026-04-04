package com.example.demo.dashboard.presentation;

import com.example.demo.auth.domain.enums.Role;
import com.example.demo.auth.domain.enums.StatutCompte;
import com.example.demo.common.response.ApiResponse;
import com.example.demo.dashboard.dto.*;
import com.example.demo.dashboard.service.DashboardAdminService;
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
}

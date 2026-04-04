package com.example.demo.recommendation.api;

import com.example.demo.common.config.JwtUser;
import com.example.demo.recommendation.dto.CrossSessionRecommendationDTO;
import com.example.demo.recommendation.service.CrossSessionRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller pour les recommandations cross-session
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/eval")
@Tag(name = "Cross-Session Recommendations", description = "Generate holistic recommendations across all sessions")
public class CrossSessionRecommendationController {
    
    private final CrossSessionRecommendationService crossSessionService;
    
    public CrossSessionRecommendationController(CrossSessionRecommendationService crossSessionService) {
        this.crossSessionService = crossSessionService;
    }
    
    /**
     * Générer recommandations cross-session pour l'utilisateur courant
     * 
     * Extrait l'utilisateur du token JWT et agrège TOUTES les sessions:
     * - Timeline d'évolution des compétences
     * - Analyse des tendances (momentum, progression, stagnation, régression)
     * - Recommandations prioritisées (P1 CRITICAL → P4 OPTIONAL)
     * - Chemin d'apprentissage suggéré
     * - Métriques globales
     * 
     * @return CrossSessionRecommendationDTO avec analyse complète
     */
    @GetMapping("/recommendations/cross-session")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(
        summary = "Get my cross-session recommendations",
        description = "Aggregate all my sessions and provide holistic learning recommendations"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<CrossSessionRecommendationDTO> getMyCrossSessionRecommendations() {
        // Extraire le JwtUser du token JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        Long userId = jwtUser.userId();
        
        log.info("Received request for cross-session recommendations - userId: {}", userId);
        
        try {
            CrossSessionRecommendationDTO recommendations = 
                crossSessionService.generateCrossSessionRecommendations(userId);
            
            log.info("Successfully generated cross-session recommendations for userId {}", userId);
            return ResponseEntity.ok(recommendations);
            
        } catch (RuntimeException e) {
            log.error("Error generating cross-session recommendations for userId {}: {}",
                userId, e.getMessage(), e);
            throw e;
        }
    }
}

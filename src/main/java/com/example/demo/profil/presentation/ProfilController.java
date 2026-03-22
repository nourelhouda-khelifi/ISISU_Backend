package com.example.demo.profil.presentation;

import com.example.demo.profil.application.ProfilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/profil")
@RequiredArgsConstructor
@Tag(name = "Profil Management", description = "Endpoints for user profile management")
@SecurityRequirement(name = "Bearer Authentication")
public class ProfilController {

    private final ProfilService profilService;

    /**
     * GET /api/v1/profil/{id}
     * Get profile of a specific user (public endpoint)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user profile by ID", description = "Retrieve profile information of a specific user")
    public ResponseEntity<ProfilResponse> getProfilById(@PathVariable Long id) {
        log.info("GET request to fetch profil for user ID: {}", id);
        ProfilResponse profilResponse = profilService.getProfilById(id);
        return ResponseEntity.ok(profilResponse);
    }

    /**
     * GET /api/v1/profil/me
     * Get authenticated user's own profile
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user's profile", description = "Retrieve the authenticated user's profile information")
    public ResponseEntity<ProfilResponse> getMyProfil() {
        Long userId = extractUserIdFromToken();
        log.info("GET request to fetch current user profil, user ID: {}", userId);
        ProfilResponse profilResponse = profilService.getMyProfil(userId);
        return ResponseEntity.ok(profilResponse);
    }

    /**
     * PUT /api/v1/profil/me
     * Update authenticated user's profile
     */
    @PutMapping("/me")
    @Operation(summary = "Update current user's profile", description = "Update the authenticated user's profile information")
    public ResponseEntity<ProfilResponse> updateProfil(
            @Valid @RequestBody UpdateProfilRequest updateProfilRequest) {
        Long userId = extractUserIdFromToken();
        log.info("PUT request to update profil for user ID: {}", userId);
        ProfilResponse profilResponse = profilService.updateProfil(userId, updateProfilRequest);
        return ResponseEntity.ok(profilResponse);
    }

    /**
     * POST /api/v1/profil/me/password
     * Change authenticated user's password
     */
    @PostMapping("/me/password")
    @Operation(summary = "Change user password", description = "Change the authenticated user's password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        Long userId = extractUserIdFromToken();
        log.info("POST request to change password for user ID: {}", userId);
        profilService.changePassword(userId, changePasswordRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * DELETE /api/v1/profil/me
     * Soft delete authenticated user (set status to INACTIF)
     */
    @DeleteMapping("/me")
    @Operation(summary = "Deactivate user account", description = "Deactivate the authenticated user's account (soft delete)")
    public ResponseEntity<Void> softDeleteUser() {
        Long userId = extractUserIdFromToken();
        log.info("DELETE request to soft delete user ID: {}", userId);
        profilService.softDeleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Extract user ID from JWT token in Security Context
     */
    private Long extractUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("User not authenticated");
        }
        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid user ID in token", e);
        }
    }
}

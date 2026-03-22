package com.example.demo.profil.application;

import com.example.demo.auth.domain.Utilisateur;
import com.example.demo.auth.domain.enums.StatutCompte;
import com.example.demo.auth.infrastructure.repository.UtilisateurRepository;
import com.example.demo.profil.presentation.ChangePasswordRequest;
import com.example.demo.profil.presentation.ProfilResponse;
import com.example.demo.profil.presentation.UpdateProfilRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfilService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Retrieve profile of a specific user by ID
     * @param userId the user ID
     * @return ProfilResponse containing user profile information
     */
    public ProfilResponse getProfilById(Long userId) {
        log.info("Fetching profil for user ID: {}", userId);
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found with id: " + userId));

        return convertToProfilResponse(utilisateur);
    }

    /**
     * Retrieve the authenticated user's profile
     * @param userId the authenticated user's ID
     * @return ProfilResponse containing user's own profile information
     */
    public ProfilResponse getMyProfil(Long userId) {
        log.info("Fetching profil for authenticated user ID: {}", userId);
        return getProfilById(userId);
    }

    /**
     * Update user's profile information
     * @param userId the user ID
     * @param updateProfilRequest the profile data to update
     * @return updated ProfilResponse
     */
    @Transactional
    public ProfilResponse updateProfil(Long userId, UpdateProfilRequest updateProfilRequest) {
        log.info("Updating profil for user ID: {}", userId);

        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found with id: " + userId));

        // Update basic personal information
        utilisateur.setNom(updateProfilRequest.getNom());
        utilisateur.setPrenom(updateProfilRequest.getPrenom());

        if (updateProfilRequest.getTelephone() != null) {
            utilisateur.setTelephone(updateProfilRequest.getTelephone());
        }

        if (updateProfilRequest.getDateNaissance() != null) {
            try {
                LocalDate dateNaissance = LocalDate.parse(updateProfilRequest.getDateNaissance(), DATE_FORMATTER);
                utilisateur.setDateNaissance(dateNaissance);
            } catch (Exception e) {
                log.warn("Invalid date format for dateNaissance: {}", updateProfilRequest.getDateNaissance());
            }
        }

        if (updateProfilRequest.getAdresse() != null) {
            utilisateur.setAdresse(updateProfilRequest.getAdresse());
        }

        if (updateProfilRequest.getCodePostal() != null) {
            utilisateur.setCodePostal(updateProfilRequest.getCodePostal());
        }

        if (updateProfilRequest.getVille() != null) {
            utilisateur.setVille(updateProfilRequest.getVille());
        }

        if (updateProfilRequest.getNationalite() != null) {
            utilisateur.setNationalite(updateProfilRequest.getNationalite());
        }

        if (updateProfilRequest.getPhotoUrl() != null) {
            utilisateur.setPhotoUrl(updateProfilRequest.getPhotoUrl());
        }

        utilisateur.setDateModification(LocalDateTime.now());

        Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateur);
        log.info("Profil updated successfully for user ID: {}", userId);

        return convertToProfilResponse(savedUtilisateur);
    }

    /**
     * Change user's password with validation
     * @param userId the user ID
     * @param changePasswordRequest containing old and new passwords
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest changePasswordRequest) {
        log.info("Changing password for user ID: {}", userId);

        // Validate that new passwords match
        if (!changePasswordRequest.getNouveauMotDePasse().equals(changePasswordRequest.getConfirmationMotDePasse())) {
            throw new RuntimeException("Passwords do not match");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found with id: " + userId));

        // Verify that old password matches
        if (!passwordEncoder.matches(changePasswordRequest.getAncienMotDePasse(), utilisateur.getMotDePasseHash())) {
            log.warn("Invalid old password attempt for user ID: {}", userId);
            throw new RuntimeException("Old password is incorrect");
        }

        // Update with new password
        String hashedPassword = passwordEncoder.encode(changePasswordRequest.getNouveauMotDePasse());
        utilisateur.setMotDePasseHash(hashedPassword);
        utilisateur.setDateModification(LocalDateTime.now());

        utilisateurRepository.save(utilisateur);
        log.info("Password changed successfully for user ID: {}", userId);
    }

    /**
     * Soft delete a user (set status to SUSPENDU)
     * @param userId the user ID
     */
    @Transactional
    public void softDeleteUser(Long userId) {
        log.info("Soft deleting user ID: {}", userId);

        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found with id: " + userId));

        utilisateur.setStatut(StatutCompte.SUSPENDU);
        utilisateur.setDateModification(LocalDateTime.now());

        utilisateurRepository.save(utilisateur);
        log.info("User ID: {} soft deleted successfully", userId);
    }

    /**
     * Convert Utilisateur entity to ProfilResponse DTO
     */
    private ProfilResponse convertToProfilResponse(Utilisateur utilisateur) {
        String dateNaissanceStr = utilisateur.getDateNaissance() != null 
            ? utilisateur.getDateNaissance().format(DATE_FORMATTER)
            : null;
            
        return ProfilResponse.builder()
                .id(utilisateur.getId())
                .email(utilisateur.getEmail())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .role(utilisateur.getRole())
                .statut(utilisateur.getStatut())
                .telephone(utilisateur.getTelephone())
                .dateNaissance(dateNaissanceStr)
                .adresse(utilisateur.getAdresse())
                .codePostal(utilisateur.getCodePostal())
                .ville(utilisateur.getVille())
                .nationalite(utilisateur.getNationalite())
                .photoUrl(utilisateur.getPhotoUrl())
                .emailVerifie(utilisateur.isEmailVerifie())
                .dateInscription(utilisateur.getDateInscription())
                .derniereConnexion(utilisateur.getDerniereConnexion())
                .dateModification(utilisateur.getDateModification())
                .build();
    }
}

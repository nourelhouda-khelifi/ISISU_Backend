package com.example.demo.auth.application;

import com.example.demo.auth.api.dto.InscriptionResponse;
import com.example.demo.auth.api.dto.InscriptionVAERequest;
import com.example.demo.auth.domain.Utilisateur;
import com.example.demo.auth.domain.enums.Role;
import com.example.demo.auth.domain.enums.StatutCompte;
import com.example.demo.auth.infrastructure.repository.UtilisateurRepository;
import com.example.demo.common.exception.BadRequestException;
import com.example.demo.common.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InscriptionVAEUseCase {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpUseCase otpUseCase;

    @Transactional
    public InscriptionResponse execute(InscriptionVAERequest request) {
        String email = normalizeEmail(request.email());

        if (utilisateurRepository.existsByEmail(email)) {
            throw new ConflictException("Un compte existe deja avec cet email.");
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .email(email)
                .motDePasseHash(passwordEncoder.encode(request.motDePasse()))
                .nom(request.nom())
                .prenom(request.prenom())
                .role(Role.CANDIDAT_VAE)
                .statut(StatutCompte.EN_ATTENTE_OTP)
                .emailVerifie(false)
                .accepteCgu(request.accepteCgu())
                .consentementDonnees(request.consentementDonnees())
                .consentementContact(request.consentementContact())
                .niveauEtudes(request.niveauEtudes())
                .secteurActivite(request.secteurActivite())
                .build();

        Utilisateur saved = utilisateurRepository.save(utilisateur);
        otpUseCase.generateAndSendOtp(email);

        return new InscriptionResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getStatut().name(),
                "Inscription VAE enregistree. Verifiez votre email pour le code OTP."
        );
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            throw new BadRequestException("Email obligatoire.");
        }
        return email.trim().toLowerCase();
    }
}

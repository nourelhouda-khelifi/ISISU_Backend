package com.example.demo.auth.application;

import com.example.demo.auth.api.dto.InscriptionFIE3Request;
import com.example.demo.auth.api.dto.InscriptionResponse;
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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InscriptionFIE3UseCase {

    private static final String ISIS_EMAIL_REGEX = "^[a-zA-Z]+\\.[a-zA-Z]+@etud\\.univ-jfc\\.fr$";

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public InscriptionResponse execute(InscriptionFIE3Request request) {
        String email = normalizeEmail(request.email());
        String codeINE = request.codeINE().trim().toUpperCase();

        if (!email.matches(ISIS_EMAIL_REGEX)) {
            throw new BadRequestException("Email ISIS invalide. Format attendu: prenom.nom@etud.univ-jfc.fr");
        }
        if (utilisateurRepository.existsByEmail(email)) {
            throw new ConflictException("Un compte existe deja avec cet email.");
        }
        if (utilisateurRepository.existsByCodeINE(codeINE)) {
            throw new ConflictException("Un compte existe deja avec ce code INE.");
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .email(email)
                .motDePasseHash(passwordEncoder.encode(request.motDePasse()))
                .nom(request.nom())
                .prenom(request.prenom())
                .role(Role.ETUDIANT_FIE3)
                .statut(StatutCompte.ACTIF)
                .emailVerifie(true)
                .dateVerificationEmail(LocalDateTime.now())
                .accepteCgu(request.accepteCgu())
                .consentementDonnees(request.consentementDonnees())
                .consentementContact(request.consentementContact())
                .codeINE(codeINE)
                .niveauFIE(request.niveauFIE())
                .promotion(request.promotion())
                .parcoursOrigine(request.parcoursOrigine())
                .build();

        Utilisateur saved = utilisateurRepository.save(utilisateur);

        return new InscriptionResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getStatut().name(),
                "Inscription FIE3 reussie. Vous pouvez vous connecter."
        );
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            throw new BadRequestException("Email obligatoire.");
        }
        return email.trim().toLowerCase();
    }
}

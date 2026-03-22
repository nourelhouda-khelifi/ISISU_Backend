package com.example.demo.auth.application;

import com.example.demo.auth.api.dto.InscriptionResponse;
import com.example.demo.auth.api.dto.VerifierOtpRequest;
import com.example.demo.auth.domain.Utilisateur;
import com.example.demo.auth.domain.enums.StatutCompte;
import com.example.demo.auth.infrastructure.repository.UtilisateurRepository;
import com.example.demo.common.exception.BadRequestException;
import com.example.demo.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerifierOtpUseCase {

    private final OtpUseCase otpUseCase;
    private final UtilisateurRepository utilisateurRepository;

    @Transactional
    public InscriptionResponse execute(VerifierOtpRequest request) {
        String email = normalizeEmail(request.email());

        otpUseCase.verifyOtpOrThrow(email, request.code());

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("Utilisateur introuvable."));

        utilisateur.setEmailVerifie(true);
        utilisateur.setDateVerificationEmail(LocalDateTime.now());
        utilisateur.setStatut(StatutCompte.ACTIF);
        utilisateurRepository.save(utilisateur);

        return new InscriptionResponse(
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getStatut().name(),
                "Email verifie avec succes. Votre compte est maintenant actif."
        );
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            throw new BadRequestException("Email obligatoire.");
        }
        return email.trim().toLowerCase();
    }
}

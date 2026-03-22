package com.example.demo.auth.application;

import com.example.demo.auth.api.dto.OtpResponse;
import com.example.demo.auth.api.dto.RenvoyerOtpRequest;
import com.example.demo.auth.domain.Utilisateur;
import com.example.demo.auth.domain.enums.StatutCompte;
import com.example.demo.auth.infrastructure.repository.UtilisateurRepository;
import com.example.demo.common.exception.BadRequestException;
import com.example.demo.common.exception.ConflictException;
import com.example.demo.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RenvoyerOtpUseCase {

    private final UtilisateurRepository utilisateurRepository;
    private final OtpUseCase otpUseCase;

    @Transactional
    public OtpResponse execute(RenvoyerOtpRequest request) {
        String email = normalizeEmail(request.email());

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Compte introuvable."));

        if (utilisateur.getStatut() == StatutCompte.ACTIF && utilisateur.isEmailVerifie()) {
            throw new ConflictException("Compte deja actif et verifie.");
        }

        otpUseCase.generateAndSendOtp(email);

        return new OtpResponse(email, "Nouveau code OTP envoye.");
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            throw new BadRequestException("Email obligatoire.");
        }
        return email.trim().toLowerCase();
    }
}

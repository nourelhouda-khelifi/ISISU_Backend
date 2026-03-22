package com.example.demo.auth.application;

import com.example.demo.auth.api.dto.LoginRequest;
import com.example.demo.auth.api.dto.LoginResponse;
import com.example.demo.auth.domain.Utilisateur;
import com.example.demo.auth.domain.enums.StatutCompte;
import com.example.demo.auth.infrastructure.repository.UtilisateurRepository;
import com.example.demo.auth.infrastructure.service.JwtService;
import com.example.demo.common.exception.BadRequestException;
import com.example.demo.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public LoginResponse execute(LoginRequest request) {
        String email = normalizeEmail(request.email());

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
            .orElseThrow(() -> new UnauthorizedException("Email ou mot de passe invalide."));

        if (!passwordEncoder.matches(request.motDePasse(), utilisateur.getMotDePasseHash())) {
            throw new UnauthorizedException("Email ou mot de passe invalide.");
        }

        if (utilisateur.getStatut() != StatutCompte.ACTIF) {
            throw new UnauthorizedException("Compte non actif. Verifiez votre OTP ou contactez l'administration.");
        }

        utilisateur.setDerniereConnexion(LocalDateTime.now());
        utilisateurRepository.save(utilisateur);

        String token = jwtService.generateAccessToken(utilisateur);

        return new LoginResponse(
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getRole().name(),
                token,
                "Bearer",
                jwtService.getAccessTokenTtlSeconds()
        );
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            throw new BadRequestException("Email obligatoire.");
        }
        return email.trim().toLowerCase();
    }
}

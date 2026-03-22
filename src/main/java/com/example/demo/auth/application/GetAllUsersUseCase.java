package com.example.demo.auth.application;

import com.example.demo.auth.api.dto.UtilisateurResponse;
import com.example.demo.auth.domain.Utilisateur;
import com.example.demo.auth.infrastructure.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllUsersUseCase {

    private final UtilisateurRepository utilisateurRepository;

    public List<UtilisateurResponse> execute() {
        return utilisateurRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private UtilisateurResponse toResponse(Utilisateur utilisateur) {
        return new UtilisateurResponse(
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getRole().toString(),
                utilisateur.getStatut().toString(),
                utilisateur.isEmailVerifie(),
                utilisateur.getDateInscription(),
                utilisateur.getDerniereConnexion()
        );
    }
}

package com.example.demo.auth.api.dto;

import java.time.LocalDateTime;

public record UtilisateurResponse(
        Long id,
        String email,
        String nom,
        String prenom,
        String role,
        String statut,
        boolean emailVerifie,
        LocalDateTime dateInscription,
        LocalDateTime derniereConnexion
) {
}

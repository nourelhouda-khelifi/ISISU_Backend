package com.example.demo.dashboard.dto;

import com.example.demo.auth.domain.enums.Role;
import com.example.demo.auth.domain.enums.StatutCompte;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Utilisateur dans le dashboard admin (liste)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurDashboardDTO {
    
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private Role role;
    private StatutCompte statut;
    private Long nombreSessions;
    private Double scoreMoyen;
    private LocalDateTime derniereConnexion;
}

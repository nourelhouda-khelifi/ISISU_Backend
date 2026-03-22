package com.example.demo.profil.presentation;

import com.example.demo.auth.domain.enums.Role;
import com.example.demo.auth.domain.enums.StatutCompte;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfilResponse {

    private Long id;
    private String email;
    private String nom;
    private String prenom;
    private Role role;
    private StatutCompte statut;
    private String telephone;
    private String dateNaissance;  // Formatted as YYYY-MM-DD
    private String adresse;
    private String codePostal;
    private String ville;
    private String nationalite;
    private String photoUrl;
    private Boolean emailVerifie;
    private LocalDateTime dateInscription;
    private LocalDateTime derniereConnexion;
    private LocalDateTime dateModification;
}

package com.example.demo.auth.domain;

import com.example.demo.auth.domain.enums.NiveauEtudes;
import com.example.demo.auth.domain.enums.NiveauFIE;
import com.example.demo.auth.domain.enums.ParcoursOrigine;
import com.example.demo.auth.domain.enums.Role;
import com.example.demo.auth.domain.enums.SecteurActivite;
import com.example.demo.auth.domain.enums.StatutCompte;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "utilisateurs",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "code_ine")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, name = "mot_de_passe_hash")
    private String motDePasseHash;

    @Column(nullable = false, length = 50)
    private String nom;

    @Column(nullable = false, length = 50)
    private String prenom;

    @Column(length = 20)
    private String telephone;

    @Column(length = 60)
    private String nationalite;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "photo_url")
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCompte statut;

    @Column(nullable = false, name = "email_verifie")
    private boolean emailVerifie = false;

    @Column(nullable = false, name = "accepte_cgu")
    private boolean accepteCgu;

    @Column(nullable = false, name = "consentement_donnees")
    private boolean consentementDonnees;

    @Column(nullable = false, name = "consentement_contact")
    private boolean consentementContact;

    @Column(name = "date_verification_email")
    private LocalDateTime dateVerificationEmail;

    @Column(nullable = false, name = "date_inscription")
    private LocalDateTime dateInscription;

    @Column(name = "derniere_connexion")
    private LocalDateTime derniereConnexion;

    @Column(name = "code_ine", unique = true, length = 11)
    private String codeINE;

    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_fie")
    private NiveauFIE niveauFIE;

    @Column(name = "promotion")
    private Integer promotion;

    @Enumerated(EnumType.STRING)
    @Column(name = "parcours_origine")
    private ParcoursOrigine parcoursOrigine;

    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_etudes")
    private NiveauEtudes niveauEtudes;

    @Enumerated(EnumType.STRING)
    @Column(name = "secteur_activite")
    private SecteurActivite secteurActivite;

    @Column(length = 255)
    private String adresse;

    @Column(length = 10)
    private String codePostal;

    @Column(length = 100)
    private String ville;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @PrePersist
    public void onCreate() {
        normalizeData();
        validateRoleConsistency();

        if (dateInscription == null) {
            dateInscription = LocalDateTime.now();
        }
        if (statut == null) {
            statut = StatutCompte.EN_ATTENTE_OTP;
        }
    }

    @PreUpdate
    public void onUpdate() {
        normalizeData();
        validateRoleConsistency();
        dateModification = LocalDateTime.now();
    }

    private void normalizeData() {
        if (email != null) {
            email = email.trim().toLowerCase();
        }
        if (nom != null) {
            nom = nom.trim();
        }
        if (prenom != null) {
            prenom = prenom.trim();
        }
        if (telephone != null) {
            telephone = telephone.trim();
        }
        if (codeINE != null) {
            codeINE = codeINE.trim().toUpperCase();
        }
    }

    private void validateRoleConsistency() {
        if (role == null) {
            throw new IllegalStateException("Le role est obligatoire.");
        }

        switch (role) {
            case ETUDIANT_FIE3 -> validateFie3Fields();
            case CANDIDAT_VAE -> validateVaeFields();
            case ADMIN -> validateAdminFields();
        }
    }

    private void validateFie3Fields() {
        if (isBlank(codeINE) || niveauFIE == null || promotion == null || parcoursOrigine == null) {
            throw new IllegalStateException("Un ETUDIANT_FIE3 doit avoir codeINE, niveauFIE, promotion et parcoursOrigine.");
        }
        if (niveauEtudes != null || secteurActivite != null) {
            throw new IllegalStateException("Un ETUDIANT_FIE3 ne doit pas avoir les champs VAE.");
        }
    }

    private void validateVaeFields() {
        if (niveauEtudes == null || secteurActivite == null) {
            throw new IllegalStateException("Un CANDIDAT_VAE doit avoir niveauEtudes et secteurActivite.");
        }
        if (codeINE != null || niveauFIE != null || promotion != null || parcoursOrigine != null) {
            throw new IllegalStateException("Un CANDIDAT_VAE ne doit pas avoir les champs FIE3.");
        }
    }

    private void validateAdminFields() {
        if (codeINE != null || niveauFIE != null || promotion != null || parcoursOrigine != null
                || niveauEtudes != null || secteurActivite != null) {
            throw new IllegalStateException("Un ADMIN ne doit pas avoir de champs specifiques FIE3/VAE.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public boolean estEtudiantFIE() {
        return role == Role.ETUDIANT_FIE3;
    }

    public boolean estCandidatVAE() {
        return role == Role.CANDIDAT_VAE;
    }

    public boolean estAdmin() {
        return role == Role.ADMIN;
    }
}

package com.example.demo.auth.api.dto;

import com.example.demo.auth.domain.enums.NiveauEtudes;
import com.example.demo.auth.domain.enums.SecteurActivite;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InscriptionVAERequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 128) String motDePasse,
        @NotBlank @Size(max = 50) String nom,
        @NotBlank @Size(max = 50) String prenom,
        @NotNull NiveauEtudes niveauEtudes,
        @NotNull SecteurActivite secteurActivite,
        @AssertTrue boolean accepteCgu,
        boolean consentementDonnees,
        boolean consentementContact
) {
}

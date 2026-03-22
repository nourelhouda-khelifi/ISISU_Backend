package com.example.demo.auth.api.dto;

import com.example.demo.auth.domain.enums.NiveauFIE;
import com.example.demo.auth.domain.enums.ParcoursOrigine;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InscriptionFIE3Request(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 128) String motDePasse,
        @NotBlank @Size(max = 50) String nom,
        @NotBlank @Size(max = 50) String prenom,
        @NotBlank @Pattern(regexp = "^[0-9]{10}[A-Z]$") String codeINE,
        @NotNull NiveauFIE niveauFIE,
        @NotNull Integer promotion,
        @NotNull ParcoursOrigine parcoursOrigine,
        @AssertTrue boolean accepteCgu,
        boolean consentementDonnees,
        boolean consentementContact
) {
}

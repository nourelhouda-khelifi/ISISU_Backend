package com.example.demo.profil.presentation;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfilRequest {

    @NotBlank(message = "Nom cannot be blank")
    @Size(min = 2, max = 100, message = "Nom must be between 2 and 100 characters")
    private String nom;

    @NotBlank(message = "Prenom cannot be blank")
    @Size(min = 2, max = 100, message = "Prenom must be between 2 and 100 characters")
    private String prenom;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Telepone format invalid")
    private String telephone;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "invalid dateNaissance format (YYYY-MM-DD)")
    private String dateNaissance;

    @Size(max = 255, message = "Adresse must not exceed 255 characters")
    private String adresse;

    @Pattern(regexp = "^[0-9]{5}$", message = "Code postal must be 5 digits")
    private String codePostal;

    @Size(max = 100, message = "Ville must not exceed 100 characters")
    private String ville;

    @Size(max = 100, message = "Nationalite must not exceed 100 characters")
    private String nationalite;

    @Size(max = 500, message = "Photo URL must not exceed 500 characters")
    private String photoUrl;
}

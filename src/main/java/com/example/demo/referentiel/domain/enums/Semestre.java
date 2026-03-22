package com.example.demo.referentiel.domain.enums;

/**
 * Semestre d'appartenance pour un module FIE3.
 * Valeurs figées : S5 et S6 (2ème et 3ème années)
 */
public enum Semestre {
    S5("Semestre 5"),
    S6("Semestre 6");

    private final String libelle;

    Semestre(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}

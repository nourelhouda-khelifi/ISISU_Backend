-- V1__create_auth_schema.sql
-- Création du schéma d'authentification et utilisateurs

-- Table des utilisateurs
CREATE TABLE IF NOT EXISTS utilisateurs (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe_hash VARCHAR(255) NOT NULL,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    telephone VARCHAR(20),
    nationalite VARCHAR(60),
    date_naissance DATE,
    photo_url TEXT,
    role VARCHAR(50) NOT NULL,
    statut VARCHAR(50) NOT NULL,
    email_verifie BOOLEAN NOT NULL DEFAULT false,
    accepte_cgu BOOLEAN NOT NULL,
    consentement_donnees BOOLEAN NOT NULL,
    consentement_contact BOOLEAN NOT NULL,
    date_verification_email TIMESTAMP,
    date_inscription TIMESTAMP NOT NULL,
    derniere_connexion TIMESTAMP,
    code_ine VARCHAR(11) UNIQUE,
    niveau_fie VARCHAR(50),
    promotion INTEGER,
    parcours_origine VARCHAR(50),
    niveau_etudes VARCHAR(50),
    secteur_activite VARCHAR(50),
    adresse VARCHAR(255),
    code_postal VARCHAR(10),
    ville VARCHAR(100),
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_email UNIQUE (email),
    CONSTRAINT uk_code_ine UNIQUE (code_ine)
);

-- Index sur les colonnes fréquemment interrogées
CREATE INDEX idx_utilisateurs_email ON utilisateurs(email);
CREATE INDEX idx_utilisateurs_statut ON utilisateurs(statut);
CREATE INDEX idx_utilisateurs_code_ine ON utilisateurs(code_ine);

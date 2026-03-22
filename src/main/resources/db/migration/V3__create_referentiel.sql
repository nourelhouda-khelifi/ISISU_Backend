-- V3__create_referentiel.sql
-- Création du schéma Référentiel FIE3

-- Table des Unités d'Enseignement
CREATE TABLE IF NOT EXISTS unites_enseignement (
    id SERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL,
    ects INTEGER NOT NULL,
    semestre VARCHAR(10) NOT NULL
);

-- Table des Modules FIE
CREATE TABLE IF NOT EXISTS modules_fie (
    id SERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    nom VARCHAR(150) NOT NULL,
    semestre VARCHAR(10) NOT NULL,
    ue_id BIGINT NOT NULL REFERENCES unites_enseignement(id),
    heures_cm INTEGER NOT NULL,
    heures_td INTEGER NOT NULL,
    heures_tp INTEGER NOT NULL,
    heures_projet INTEGER NOT NULL,
    heures_total INTEGER NOT NULL,
    evaluable BOOLEAN NOT NULL DEFAULT false,
    prerequis_texte TEXT
);

-- Table de jonction : modules prérequis
CREATE TABLE IF NOT EXISTS modules_prerequis (
    module_id BIGINT NOT NULL REFERENCES modules_fie(id),
    prerequis_id BIGINT NOT NULL REFERENCES modules_fie(id),
    PRIMARY KEY (module_id, prerequis_id)
);

-- Table des Compétences
CREATE TABLE IF NOT EXISTS competences (
    id SERIAL PRIMARY KEY,
    intitule VARCHAR(255) NOT NULL,
    description TEXT,
    numero_ordre INTEGER NOT NULL,
    niveau_attendu INTEGER NOT NULL,
    poids DOUBLE PRECISION NOT NULL,
    module_id BIGINT NOT NULL REFERENCES modules_fie(id)
);

-- Table de jonction : compétences prérequis
CREATE TABLE IF NOT EXISTS competences_prerequis (
    competence_id BIGINT NOT NULL REFERENCES competences(id),
    prerequis_id BIGINT NOT NULL REFERENCES competences(id),
    PRIMARY KEY (competence_id, prerequis_id)
);

-- Index pour améliorer les performances
CREATE INDEX idx_modules_fie_ue_id ON modules_fie(ue_id);
CREATE INDEX idx_modules_fie_evaluable ON modules_fie(evaluable);
CREATE INDEX idx_competences_module_id ON competences(module_id);

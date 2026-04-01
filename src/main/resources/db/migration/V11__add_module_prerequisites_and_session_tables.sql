-- V11__add_module_prerequisites_and_session_tables.sql

-- ─────────────────────────────────────────────────────────────
-- 1. AJOUTER COLONNES À ModuleFIE pour le tri topologique
-- ─────────────────────────────────────────────────────────────

ALTER TABLE modules_fie ADD COLUMN niveau INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE modules_fie ADD COLUMN ordre_niveau INTEGER DEFAULT 0 NOT NULL;

-- ─────────────────────────────────────────────────────────────
-- 2. CRÉER TABLE MODULE_PREREQUIS (relation ManyToMany)
-- ─────────────────────────────────────────────────────────────

CREATE TABLE module_prerequis (
    module_id BIGINT NOT NULL,
    prerequis_id BIGINT NOT NULL,
    PRIMARY KEY (module_id, prerequis_id),
    FOREIGN KEY (module_id) REFERENCES modules_fie(id) ON DELETE CASCADE,
    FOREIGN KEY (prerequis_id) REFERENCES modules_fie(id) ON DELETE CASCADE
);

-- ─────────────────────────────────────────────────────────────
-- 3. Remplir le NIVEAU 0 (sans prérequis)
-- ─────────────────────────────────────────────────────────────

UPDATE modules_fie SET niveau = 0, ordre_niveau = 1 WHERE code = 'E3-1-IN-1'; -- POO Java
UPDATE modules_fie SET niveau = 0, ordre_niveau = 2 WHERE code = 'E3-1-IN-3'; -- Gestion de projet
UPDATE modules_fie SET niveau = 0, ordre_niveau = 3 WHERE code = 'E3-1-ID-1'; -- Systèmes d'info
UPDATE modules_fie SET niveau = 0, ordre_niveau = 4 WHERE code = 'E3-1-ID-2'; -- Base de données
UPDATE modules_fie SET niveau = 0, ordre_niveau = 5 WHERE code = 'E3-1-IS-2'; -- Fondamentaux IA

-- ─────────────────────────────────────────────────────────────
-- 4. Ajouter les prérequis NIVEAU 1
-- ─────────────────────────────────────────────────────────────

-- GL ← prérequis POO
INSERT INTO module_prerequis (module_id, prerequis_id)
SELECT m1.id, m2.id FROM modules_fie m1, modules_fie m2
WHERE m1.code = 'E3-1-IN-2' AND m2.code = 'E3-1-IN-1';

-- Épidémiologie ← prérequis IA
INSERT INTO module_prerequis (module_id, prerequis_id)
SELECT m1.id, m2.id FROM modules_fie m1, modules_fie m2
WHERE m1.code = 'E3-1-ID-3' AND m2.code = 'E3-1-IS-2';

-- CCU ← prérequis Gestion projet
INSERT INTO module_prerequis (module_id, prerequis_id)
SELECT m1.id, m2.id FROM modules_fie m1, modules_fie m2
WHERE m1.code = 'E3-2-IN-3' AND m2.code = 'E3-1-IN-3';

-- Imagerie ← prérequis IA
INSERT INTO module_prerequis (module_id, prerequis_id)
SELECT m1.id, m2.id FROM modules_fie m1, modules_fie m2
WHERE m1.code = 'E3-2-IS-4' AND m2.code = 'E3-1-IS-2';

-- Assigner NIVEAU 1
UPDATE modules_fie SET niveau = 1, ordre_niveau = 1 WHERE code = 'E3-1-IN-2'; -- GL
UPDATE modules_fie SET niveau = 1, ordre_niveau = 2 WHERE code = 'E3-1-ID-3'; -- Épidémiologie
UPDATE modules_fie SET niveau = 1, ordre_niveau = 3 WHERE code = 'E3-2-IN-3'; -- CCU
UPDATE modules_fie SET niveau = 1, ordre_niveau = 4 WHERE code = 'E3-2-IS-4'; -- Imagerie

-- ─────────────────────────────────────────────────────────────
-- 5. Ajouter les prérequis NIVEAU 2
-- ─────────────────────────────────────────────────────────────

-- WebTech ← prérequis POO + BDD + GL
INSERT INTO module_prerequis (module_id, prerequis_id)
SELECT m1.id, m2.id FROM modules_fie m1, modules_fie m2
WHERE m1.code = 'E3-2-IN-1' AND m2.code = 'E3-1-IN-1'; -- POO

INSERT INTO module_prerequis (module_id, prerequis_id)
SELECT m1.id, m2.id FROM modules_fie m1, modules_fie m2
WHERE m1.code = 'E3-2-IN-1' AND m2.code = 'E3-1-ID-2'; -- BDD

INSERT INTO module_prerequis (module_id, prerequis_id)
SELECT m1.id, m2.id FROM modules_fie m1, modules_fie m2
WHERE m1.code = 'E3-2-IN-1' AND m2.code = 'E3-1-IN-2'; -- GL

-- DevOps ← prérequis WebTech
INSERT INTO module_prerequis (module_id, prerequis_id)
SELECT m1.id, m2.id FROM modules_fie m1, modules_fie m2
WHERE m1.code = 'E3-2-IN-2' AND m2.code = 'E3-2-IN-1';

-- Assigner NIVEAU 2
UPDATE modules_fie SET niveau = 2, ordre_niveau = 1 WHERE code = 'E3-2-IN-1'; -- WebTech
UPDATE modules_fie SET niveau = 2, ordre_niveau = 2 WHERE code = 'E3-2-IN-2'; -- DevOps

-- ─────────────────────────────────────────────────────────────
-- 6. CRÉER TABLE SESSION_TEST
-- ─────────────────────────────────────────────────────────────

CREATE TABLE session_test (
    id BIGSERIAL PRIMARY KEY,
    utilisateur_id BIGINT NOT NULL,
    date_debut TIMESTAMP NOT NULL,
    date_fin TIMESTAMP,
    statut VARCHAR(50) NOT NULL DEFAULT 'EN_COURS',
    duree_max_secondes INTEGER NOT NULL DEFAULT 7200,
    numero_session INTEGER NOT NULL DEFAULT 1,
    ordre_modules_json TEXT,
    raison VARCHAR(100),
    date_abandon TIMESTAMP,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE
);

-- ─────────────────────────────────────────────────────────────
-- 7. CRÉER TABLE QUESTION_SESSION
-- ─────────────────────────────────────────────────────────────

CREATE TABLE question_session (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    ordre INTEGER NOT NULL,
    type VARCHAR(50) NOT NULL DEFAULT 'NORMALE',
    est_repondue BOOLEAN NOT NULL DEFAULT FALSE,
    est_correcte BOOLEAN,
    FOREIGN KEY (session_id) REFERENCES session_test(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- ─────────────────────────────────────────────────────────────
-- 8. CRÉER TABLE REPONSE_ETUDIANT
-- ─────────────────────────────────────────────────────────────

CREATE TABLE reponse_etudiant (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    question_session_id BIGINT NOT NULL,
    choix_selectionnes_json TEXT,
    reponse_texte TEXT,
    est_correcte BOOLEAN NOT NULL,
    duree_reaction_secondes INTEGER,
    date_reponse TIMESTAMP NOT NULL,
    FOREIGN KEY (session_id) REFERENCES session_test(id) ON DELETE CASCADE,
    FOREIGN KEY (question_session_id) REFERENCES question_session(id) ON DELETE CASCADE
);

-- ─────────────────────────────────────────────────────────────
-- 9. CRÉER TABLE SCORE_COMPETENCE
-- ─────────────────────────────────────────────────────────────

CREATE TABLE score_competence (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    competence_id BIGINT NOT NULL,
    score_obtenu NUMERIC(5,4) NOT NULL,
    statut VARCHAR(50) NOT NULL,
    niveau_atteint VARCHAR(50) NOT NULL DEFAULT 'NON_DEMARRE',
    nb_questions INTEGER NOT NULL DEFAULT 0,
    nb_bonnes_reponses INTEGER NOT NULL DEFAULT 0,
    score_precedent NUMERIC(5,4),
    evolution_pourcentage NUMERIC(5,2),
    FOREIGN KEY (session_id) REFERENCES session_test(id) ON DELETE CASCADE,
    FOREIGN KEY (competence_id) REFERENCES competences(id) ON DELETE CASCADE
);

-- ─────────────────────────────────────────────────────────────
-- 10. CRÉER INDEXES pour performances
-- ─────────────────────────────────────────────────────────────

CREATE INDEX idx_session_test_utilisateur ON session_test(utilisateur_id);
CREATE INDEX idx_session_test_statut ON session_test(statut);
CREATE INDEX idx_question_session_session ON question_session(session_id);
CREATE INDEX idx_question_session_ordre ON question_session(session_id, ordre);
CREATE INDEX idx_reponse_etudiant_session ON reponse_etudiant(session_id);
CREATE INDEX idx_score_competence_session ON score_competence(session_id);

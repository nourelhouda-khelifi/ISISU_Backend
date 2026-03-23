-- ══════════════════════════════════════════════════════════════════════════
-- V8__create_questions_module.sql
-- Banque de Questions — Module 3
--
-- Tables :
--   - questions          : Énoncés, types, difficultés
--   - choix              : Choix de réponse
--   - questions_competences : Liaison ManyToMany
--
-- ══════════════════════════════════════════════════════════════════════════

-- ═══════════════════════════════════════════════════════════════════════════
-- 1. TABLE : questions
-- ═══════════════════════════════════════════════════════════════════════════

CREATE TABLE IF NOT EXISTS questions (
    id BIGSERIAL PRIMARY KEY,
    enonce TEXT NOT NULL,
    type VARCHAR(50) NOT NULL
        CHECK (type IN ('QCM_SIMPLE', 'QCM_MULTIPLE', 'VRAI_FAUX', 'APPARIEMENT', 'ORDRE', 'TEXTE_TROU')),
    difficulte VARCHAR(50) NOT NULL
        CHECK (difficulte IN ('FACILE', 'MOYEN', 'DIFFICILE')),
    duree_secondes INTEGER NOT NULL DEFAULT 60,
    actif BOOLEAN NOT NULL DEFAULT true,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_questions_type 
    ON questions(type);

CREATE INDEX IF NOT EXISTS idx_questions_difficulte 
    ON questions(difficulte);

CREATE INDEX IF NOT EXISTS idx_questions_actif 
    ON questions(actif);

-- ═══════════════════════════════════════════════════════════════════════════
-- 2. TABLE : choix
-- ═══════════════════════════════════════════════════════════════════════════

CREATE TABLE IF NOT EXISTS choix (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    contenu TEXT NOT NULL,
    est_correct BOOLEAN NOT NULL DEFAULT false,
    ordre INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_choix_question_id 
    ON choix(question_id);

CREATE INDEX IF NOT EXISTS idx_choix_est_correct 
    ON choix(est_correct);

-- ═══════════════════════════════════════════════════════════════════════════
-- 3. TABLE : questions_competences (liaison ManyToMany)
-- ═══════════════════════════════════════════════════════════════════════════

CREATE TABLE IF NOT EXISTS questions_competences (
    question_id BIGINT NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    competence_id BIGINT NOT NULL REFERENCES competences(id) ON DELETE CASCADE,
    PRIMARY KEY (question_id, competence_id)
);

CREATE INDEX IF NOT EXISTS idx_questions_competences_competence_id 
    ON questions_competences(competence_id);

-- ═══════════════════════════════════════════════════════════════════════════
-- ✅ Migration complète — prêt pour les tests
-- ═══════════════════════════════════════════════════════════════════════════

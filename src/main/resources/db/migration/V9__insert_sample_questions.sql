-- ══════════════════════════════════════════════════════════════════════════
-- V9__insert_sample_questions.sql
-- Banque de Questions — Données d'exemple
--
-- Insère 6 questions exemples couvrant tous les types :
--   - 1 QCM_SIMPLE
--   - 1 QCM_MULTIPLE
--   - 1 VRAI_FAUX
--   - 1 ORDRE
--   - 1 APPARIEMENT
--   - 1 TEXTE_TROU
--
-- ══════════════════════════════════════════════════════════════════════════

-- ═══════════════════════════════════════════════════════════════════════════
-- 1. QCM_SIMPLE — Compétence de BDD (id=20 "SQL complexes")
-- ═══════════════════════════════════════════════════════════════════════════

INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation)
VALUES (
    'Quelle clause SQL permet de filtrer les groupes ?',
    'QCM_SIMPLE',
    'MOYEN',
    60,
    true,
    NOW()
);

-- Récupérer l'ID de la dernière question et insérer les choix
WITH q AS (
    SELECT MAX(id) as qid FROM questions WHERE enonce = 'Quelle clause SQL permet de filtrer les groupes ?'
)
INSERT INTO choix (question_id, contenu, est_correct, ordre)
SELECT qid, contenu, est_correct, ordre FROM q
CROSS JOIN (
    VALUES
        ('WHERE', false, 1),
        ('HAVING', true, 2),
        ('FILTER', false, 3),
        ('ORDER BY', false, 4)
) AS choices(contenu, est_correct, ordre);

-- Lier Q1 à compétence BDD (id=20)
WITH q AS (
    SELECT MAX(id) as qid FROM questions WHERE enonce = 'Quelle clause SQL permet de filtrer les groupes ?'
)
INSERT INTO questions_competences (question_id, competence_id)
SELECT qid, 20 FROM q;

-- ═══════════════════════════════════════════════════════════════════════════
-- 2. QCM_MULTIPLE — Compétence POO (id=1)
-- ═══════════════════════════════════════════════════════════════════════════

INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation)
VALUES (
    'Quels sont les principes SOLID ? (sélectionner plusieurs réponses)',
    'QCM_MULTIPLE',
    'DIFFICILE',
    90,
    true,
    NOW()
);

WITH q AS (
    SELECT MAX(id) as qid FROM questions WHERE enonce = 'Quels sont les principes SOLID ? (sélectionner plusieurs réponses)'
)
INSERT INTO choix (question_id, contenu, est_correct, ordre)
SELECT qid, contenu, est_correct, ordre FROM q
CROSS JOIN (
    VALUES
        ('Single Responsibility Principle', true, 1),
        ('Open/Closed Principle', true, 2),
        ('Fast Loading Pattern', false, 3),
        ('Liskov Substitution Principle', true, 4)
) AS choices(contenu, est_correct, ordre);

WITH q AS (
    SELECT MAX(id) as qid FROM questions WHERE enonce = 'Quels sont les principes SOLID ? (sélectionner plusieurs réponses)'
)
INSERT INTO questions_competences (question_id, competence_id)
SELECT qid, 1 FROM q;

-- ═══════════════════════════════════════════════════════════════════════════
-- 3. VRAI_FAUX — Compétence BDD (id=20)
-- ═══════════════════════════════════════════════════════════════════════════

INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation)
VALUES (
    'Une clé étrangère peut être NULL',
    'VRAI_FAUX',
    'FACILE',
    30,
    true,
    NOW()
);

WITH q AS (
    SELECT MAX(id) as qid FROM questions WHERE enonce = 'Une clé étrangère peut être NULL'
)
INSERT INTO choix (question_id, contenu, est_correct, ordre)
SELECT qid, contenu, est_correct, ordre FROM q
CROSS JOIN (
    VALUES
        ('Vrai', true, 1),
        ('Faux', false, 2)
) AS choices(contenu, est_correct, ordre);

WITH q AS (
    SELECT MAX(id) as qid FROM questions WHERE enonce = 'Une clé étrangère peut être NULL'
)
INSERT INTO questions_competences (question_id, competence_id)
SELECT qid, 20 FROM q;

-- ═══════════════════════════════════════════════════════════════════════════
-- 4. ORDRE — Compétence "Modèle OSI" (id=3)
-- ═══════════════════════════════════════════════════════════════════════════

INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation)
VALUES (
    'Remettez les couches OSI dans le bon ordre (bas → haut)',
    'ORDRE',
    'MOYEN',
    75,
    true,
    NOW()
);

WITH q AS (
    SELECT MAX(id) as qid FROM questions WHERE enonce = 'Remettez les couches OSI dans le bon ordre (bas → haut)'
)
INSERT INTO choix (question_id, contenu, est_correct, ordre)
SELECT qid, contenu, est_correct, ordre FROM q
CROSS JOIN (
    VALUES
        ('Couche Physique', true, 1),
        ('Couche Liaison', true, 2),
        ('Couche Réseau', true, 3),
        ('Couche Transport', true, 4)
) AS choices(contenu, est_correct, ordre);

WITH q AS (
    SELECT MAX(id) as qid FROM questions WHERE enonce = 'Remettez les couches OSI dans le bon ordre (bas → haut)'
)
INSERT INTO questions_competences (question_id, competence_id)
SELECT qid, 3 FROM q;

-- ═══════════════════════════════════════════════════════════════════════════
-- 5. APPARIEMENT — Compétence POO (id=1)
-- ═══════════════════════════════════════════════════════════════════════════

INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation)
VALUES (
    'Reliez chaque concept Java à sa définition',
    'APPARIEMENT',
    'MOYEN',
    60,
    true,
    NOW()
);

WITH q AS (
    SELECT MAX(id) as qid FROM questions WHERE enonce = 'Reliez chaque concept Java à sa définition'
)
INSERT INTO choix (question_id, contenu, est_correct, ordre)
SELECT qid, contenu, est_correct, ordre FROM q
CROSS JOIN (
    VALUES
        ('Héritage', true, 1),
        ('extends', true, 1),
        ('Interface', true, 2),
        ('implements', true, 2),
        ('Encapsulation', true, 3),
        ('private + getters', true, 3)
) AS choices(contenu, est_correct, ordre);

WITH q AS (
    SELECT MAX(id) as qid FROM questions WHERE enonce = 'Reliez chaque concept Java à sa définition'
)
INSERT INTO questions_competences (question_id, competence_id)
SELECT qid, 1 FROM q;

-- ═══════════════════════════════════════════════════════════════════════════
-- 6. TEXTE_TROU — Compétence BDD (id=20)
-- ═══════════════════════════════════════════════════════════════════════════

INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation)
VALUES (
    'Complétez : SELECT * FROM etudiants _____ age > 18',
    'TEXTE_TROU',
    'FACILE',
    30,
    true,
    NOW()
);

WITH q AS (
    SELECT MAX(id) as qid FROM questions WHERE enonce = 'Complétez : SELECT * FROM etudiants _____ age > 18'
)
INSERT INTO choix (question_id, contenu, est_correct, ordre)
SELECT qid, contenu, est_correct, ordre FROM q
CROSS JOIN (
    VALUES
        ('WHERE', true, 1)
) AS choices(contenu, est_correct, ordre);

WITH q AS (
    SELECT MAX(id) as qid FROM questions WHERE enonce = 'Complétez : SELECT * FROM etudiants _____ age > 18'
)
INSERT INTO questions_competences (question_id, competence_id)
SELECT qid, 20 FROM q;

-- ═══════════════════════════════════════════════════════════════════════════
-- ✅ 6 questions d'exemple insertées — prêt pour tester l'API
-- ═══════════════════════════════════════════════════════════════════════════

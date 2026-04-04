-- ══════════════════════════════════════════════════════════════════════════════════
-- V19__insert_additional_questions.sql
-- Questions supplémentaires pour tous les modules remainants
-- ══════════════════════════════════════════════════════════════════════════════════

-- Q31: Systèmes d'information - Architecture
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Quels composants forment un système d''information ?', 'QCM_MULTIPLE', 'MOYEN', 60, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Données', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Processus', true, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Technologie', true, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Qualité', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 15;

-- Q32: Systèmes d'information - Bases de données
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Une base de données relationnelle stocke les données en tables', 'VRAI_FAUX', 'FACILE', 30, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux', false, 2;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 16;

-- Q33: Systèmes d'information - ETL
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('ETL signifie ?', 'QCM_SIMPLE', 'MOYEN', 45, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Extract, Transform, Load', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Encrypt, Transmit, List', false, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Edit, Table, Link', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Export, Target, Level', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 17;

-- Q34: Systèmes d'information - Master Data
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Master data management garantit la qualité des données', 'VRAI_FAUX', 'MOYEN', 40, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux', false, 2;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 18;

-- Q35: Épidémiologie - Concepts fondamentaux
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Qu''étudie principalement l''épidémiologie ?', 'QCM_SIMPLE', 'FACILE', 30, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Les maladies dans les populations', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Les traitements individuels', false, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Les structures hospitalières', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'La biologie moléculaire', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 23;

-- Q36: Épidémiologie - Mesures d''incidence
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('L''incidence mesure les cas nouveaux pendant une période', 'VRAI_FAUX', 'MOYEN', 40, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux', false, 2;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 24;

-- Q37: Conception centrée utilisateur - Principes
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('L''UX (User Experience) met l''utilisateur au centre', 'QCM_SIMPLE', 'FACILE', 30, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux', false, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Partiellement', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Seulement en web', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 39;

-- Q38: Conception centrée utilisateur - Prototypage
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Un prototype permet de tester rapidement des idées', 'VRAI_FAUX', 'MOYEN', 40, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux', false, 2;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 40;

-- Q39: Conception centrée utilisateur - Accessibilité
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('L''accessibilité concerne les personnes handicapées ?', 'QCM_SIMPLE', 'FACILE', 30, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai, c''est pour tous', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux, seulement programmeurs', false, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Seulement pour mobiles', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Pas important', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 41;

-- Q40: Imagerie médicale - Bases
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Quels types d''imagerie existent en médecine ?', 'QCM_MULTIPLE', 'MOYEN', 60, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Radiographie', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'IRM', true, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Échographie', true, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Endoscopie', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 42;

-- Q41: AI - Clustering
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Le clustering est un problème d''apprentissage non-supervisé', 'VRAI_FAUX', 'MOYEN', 40, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux', false, 2;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 26;

-- Q42: AI - Gradient Descent
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('La descente de gradient minimise la fonction de perte', 'QCM_SIMPLE', 'DIFFICILE', 75, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux', false, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Seulement en classification', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Pas utilisée en 2026', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 27;

-- Q43: AI - Transformers
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Les transformers utilisent le mécanisme d''attention ?', 'QCM_SIMPLE', 'DIFFICILE', 60, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux - CNN seulement', false, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux - RNN seulement', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Non implémentable', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 28;

-- Questions bonus pour enrichir les compétences existantes

-- Q44: POO - Classes abstraites
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Une classe abstraite peut avoir des méthodes concrètes', 'VRAI_FAUX', 'MOYEN', 40, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux', false, 2;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 3;

-- Q45: BD - Transactions
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('ACID garantit la fiabilité des transactions BD', 'QCM_SIMPLE', 'MOYEN', 45, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux', false, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Seulement MySQL', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Pas en PostgreSQL', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 21;

-- Q46: Web - HTTP
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('HTTP est un protocole stateless', 'VRAI_FAUX', 'MOYEN', 40, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux', false, 2;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 32;

-- Q47: DevOps - Infrastructure as Code
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Infrastructure as Code permet de versionner l''infrastructure', 'VRAI_FAUX', 'MOYEN', 40, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux', false, 2;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 37;

-- Q48: Projet - Métriques
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Les métriques permettent de mesurer la progression', 'QCM_SIMPLE', 'FACILE', 30, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux', false, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Seulement en Kanban', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Inutile en Agile', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 13;

-- Vérification
SELECT 'Migration V19 complétée. Questions totales: ' || COUNT(*) as info FROM questions;

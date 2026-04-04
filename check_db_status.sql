-- Vérifier les données disponibles

-- Sessions existantes
SELECT id, utilisateur_id, statut, date_debut, date_fin
FROM session_test
LIMIT 10;

-- Nombre total de sessions
SELECT COUNT(*) as total_sessions FROM session_test;

-- Nombre total de réponses
SELECT COUNT(*) as total_reponses FROM reponse_etudiant;

-- Nombre total de scores
SELECT COUNT(*) as total_scores FROM score_competence;

-- Questions disponibles
SELECT COUNT(*) as total_questions FROM questions;

-- 1. Statut de la session 2
SELECT id, statut, date_debut, date_fin
FROM session_test
WHERE id = 2;

-- 2. Scores calculés pour session 2
SELECT COUNT(*) as nb_scores
FROM score_competence
WHERE session_id = 2;

-- 3. Réponses enregistrées session 2
SELECT COUNT(*) as nb_reponses,
       SUM(CASE WHEN est_correcte THEN 1 ELSE 0 END) as nb_correctes
FROM reponse_etudiant
WHERE session_id = 2;

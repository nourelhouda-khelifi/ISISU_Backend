-- Analyser les scores détaillés de session 2

-- 1. Scores par compétence session 2
SELECT id, competence_id, score_obtenu, statut, niveau_atteint
FROM score_competence
WHERE session_id = 2
ORDER BY id;

-- 2. Les réponses détaillées
SELECT r.id, r.est_correcte, q.enonce, qs.question_id
FROM reponse_etudiant r
JOIN question_session qs ON r.question_session_id = qs.id
JOIN questions q ON qs.question_id = q.id
WHERE r.session_id = 2
ORDER BY r.id;

-- 3. Compétences liées aux questions répondues
SELECT DISTINCT c.id, c.intitule, COUNT(r.id) as nb_reponses
FROM questions_competences qc
JOIN competences c ON qc.competence_id = c.id
JOIN questions q ON qc.question_id = q.id
JOIN question_session qs ON qs.question_id = q.id
LEFT JOIN reponse_etudiant r ON r.question_session_id = qs.id
WHERE qs.session_id = 2
GROUP BY c.id, c.intitule;

-- 4. Score global calculé vs stocké
SELECT 
  AVG(score_obtenu) as score_moyen_theoretical,
  COUNT(*) as nb_competences
FROM score_competence
WHERE session_id = 2;

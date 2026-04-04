-- Add 15 more questions for testing
INSERT INTO questions (type, enonce, duree_secondes, difficulte, actif, date_creation) VALUES
('QCM_SIMPLE', 'Qu''est-ce qu''une analyse?', 30, 'FACILE', true, NOW()),
('VRAI_FAUX', 'L''analyse est importante.', 20, 'FACILE', true, NOW()),
('QCM_SIMPLE', 'Quel est le but principal?', 30, 'FACILE', true, NOW()),
('QCM_SIMPLE', 'Comment procéder correctement?', 30, 'FACILE', true, NOW()),
('QCM_SIMPLE', 'Quels sont les outils nécessaires?', 30, 'FACILE', true, NOW()),
('APPARIEMENT', 'Associer les concepts clés', 60, 'MOYEN', true, NOW()),
('QCM_SIMPLE', 'Quelle approche est recommandée?', 45, 'MOYEN', true, NOW()),
('ORDRE', 'Ordonnez les étapes du processus', 60, 'MOYEN', true, NOW()),
('QCM_MULTIPLE', 'Quels sont les risques potentiels?', 60, 'MOYEN', true, NOW()),
('TEXTE_TROU', 'Le processus nécessite ___ et ___.', 45, 'MOYEN', true, NOW()),
('QCM_MULTIPLE', 'Quels problèmes complexes doivent être adressés?', 90, 'DIFFICILE', true, NOW()),
('APPARIEMENT', 'Associer défis architecturaux et solutions', 120, 'DIFFICILE', true, NOW()),
('TEXTE_TROU', 'Une bonne solution doit capturer les ___, ___, et ___.', 90, 'DIFFICILE', true, NOW()),
('ORDRE', 'Ordonnez par niveau de complexité croissante', 120, 'DIFFICILE', true, NOW()),
('VRAI_FAUX', 'Les architectures complexes requirent une analyse approfondie.', 30, 'DIFFICILE', true, NOW());

SELECT COUNT(*) as total_questions FROM questions;
SELECT COUNT(*) as facile FROM questions WHERE difficulte = 'FACILE';
SELECT COUNT(*) as moyen FROM questions WHERE difficulte = 'MOYEN';
SELECT COUNT(*) as difficile FROM questions WHERE difficulte = 'DIFFICILE';

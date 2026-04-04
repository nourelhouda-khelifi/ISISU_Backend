-- Migration V16: Ajouter plus de questions pour tester les sessions complètes
-- Objectif: Avoir 20+ questions totales par session

-- GROUPE 1: Questions niveau FACILE
INSERT INTO questions (type, enonce, duree_secondes, difficulte, actif, date_creation)
VALUES 
('QCM_SIMPLE', 'Qu''est-ce qu''une analyse de domaine?', 30, 'FACILE', true, NOW()),
('VRAI_FAUX', 'L''analyse de domaine aide à comprendre le contexte métier.', 20, 'FACILE', true, NOW()),
('QCM_MULTIPLE', 'Parmi les suivants, lesquels sont des pratiques reconnues?', 45, 'FACILE', true, NOW()),
('QCM_SIMPLE', 'Quel est le premier pas d''une analyse?', 30, 'FACILE', true, NOW()),
('VRAI_FAUX', 'Les données structurées facilitent l''analyse.', 20, 'FACILE', true, NOW());

-- GROUPE 2: Questions niveau MOYEN
INSERT INTO questions (type, enonce, duree_secondes, difficulte, actif, date_creation)
VALUES 
('APPARIEMENT', 'Associez les termes à leurs définitions', 60, 'MOYEN', true, NOW()),
('QCM_SIMPLE', 'Quel niveau de profondeur convient le mieux?', 45, 'MOYEN', true, NOW()),
('ORDRE', 'Ordonnez les étapes logiques', 60, 'MOYEN', true, NOW()),
('QCM_MULTIPLE', 'Quelles sont les meilleures approches?', 60, 'MOYEN', true, NOW()),
('TEXTE_TROU', 'L''analyse requiert ___ et ___.', 45, 'MOYEN', true, NOW());

-- GROUPE 3: Questions niveau DIFFICILE  
INSERT INTO questions (type, enonce, duree_secondes, difficulte, actif, date_creation)
VALUES 
('QCM_MULTIPLE', 'Quels problèmes complexes faut-il adresser?', 90, 'DIFFICILE', true, NOW()),
('APPARIEMENT', 'Associez les solutions aux défis', 120, 'DIFFICILE', true, NOW()),
('TEXTE_TROU', 'La modélisation doit capturer les ___, les ___ et les ___.', 90, 'DIFFICILE', true, NOW()),
('ORDRE', 'Ordonnez les étapes de complexité croissante', 120, 'DIFFICILE', true, NOW()),
('VRAI_FAUX', 'Les architectures complexes nécessitent une analyse approfondie.', 30, 'DIFFICILE', true, NOW());

-- Vérification
SELECT COUNT(*) as nouvelles_questions FROM questions WHERE date_creation > NOW() - INTERVAL '1 minute';

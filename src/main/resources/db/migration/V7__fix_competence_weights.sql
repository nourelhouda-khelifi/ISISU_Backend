-- V7__fix_competence_weights.sql
-- Correction des poids des compétences - Option 1: Poids égaux par module
-- 
-- Règle: Pour chaque module, chaque compétence a le même poids
-- poids = 1.0 / nombre_de_competences_du_module
--
-- Exemple:
--   - POO Java: 6 compétences → poids = 1/6 ≈ 0.166667 pour chaque
--   - Base de données: 4 compétences → poids = 1/4 = 0.25 pour chaque
--
-- Justification: Aucun enseignant n'a imposé de poids officiels.
-- Cette approche est neutre et honnête - tous les compétences
-- d'un module ont la même importance a priori.
-- Les enseignants pourront les ajuster via admin panel plus tard.

-- ============================================================================
-- Module E3-1-IN-1 : POO Java (6 compétences → 1/6 ≈ 0.166667)
-- ============================================================================
UPDATE competences SET poids = 0.166667 WHERE module_id = 1;

-- ============================================================================
-- Module E3-1-IN-2 : Génie Logiciel (4 compétences → 1/4 = 0.25)
-- ============================================================================
UPDATE competences SET poids = 0.25 WHERE module_id = 2;

-- ============================================================================
-- Module E3-1-IN-3 : Gestion de projet (4 compétences → 1/4 = 0.25)
-- ============================================================================
UPDATE competences SET poids = 0.25 WHERE module_id = 3;

-- ============================================================================
-- Module E3-1-ID-1 : Systèmes d'information (3 compétences → 1/3 ≈ 0.333333)
-- ============================================================================
UPDATE competences SET poids = 0.333333 WHERE module_id = 4;

-- ============================================================================
-- Module E3-1-ID-2 : Base de données (4 compétences → 1/4 = 0.25)
-- ============================================================================
UPDATE competences SET poids = 0.25 WHERE module_id = 5;

-- ============================================================================
-- Module E3-1-ID-3 : Épidémiologie & santé données (4 compétences → 1/4 = 0.25)
-- ============================================================================
UPDATE competences SET poids = 0.25 WHERE module_id = 6;

-- ============================================================================
-- Module E3-1-IS-2 : Fondamentaux IA (4 compétences → 1/4 = 0.25)
-- ============================================================================
UPDATE competences SET poids = 0.25 WHERE module_id = 7;

-- ============================================================================
-- Module E3-2-IN-1 : Technologies Web (4 compétences → 1/4 = 0.25)
-- ============================================================================
UPDATE competences SET poids = 0.25 WHERE module_id = 8;

-- ============================================================================
-- Module E3-2-IN-2 : DevOps et réseaux (4 compétences → 1/4 = 0.25)
-- ============================================================================
UPDATE competences SET poids = 0.25 WHERE module_id = 9;

-- ============================================================================
-- Module E3-2-IN-3 : Conception centrée utilisateur (2 compétences → 1/2 = 0.5)
-- ============================================================================
UPDATE competences SET poids = 0.5 WHERE module_id = 10;

-- ============================================================================
-- Module E3-2-IS-4 : Imagerie médicale (3 compétences → 1/3 ≈ 0.333333)
-- ============================================================================
UPDATE competences SET poids = 0.333333 WHERE module_id = 11;

-- ============================================================================
-- VÉRIFICATION - Toutes les sommes doivent être 1.0
-- ============================================================================
-- SELECT m.code, ROUND(SUM(c.poids)::numeric, 6) as somme
-- FROM modules_fie m
-- JOIN competences c ON c.module_id = m.id
-- GROUP BY m.id, m.code
-- ORDER BY m.code;

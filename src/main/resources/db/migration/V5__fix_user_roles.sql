-- V5__fix_user_roles.sql
-- Fix user roles to match Role enum values

UPDATE utilisateurs 
SET role = 'ETUDIANT_FIE3' 
WHERE role IN ('STUDENT', 'TEACHER', 'STUDENT_FIE3', 'ETUDIANT');

UPDATE utilisateurs 
SET statut = 'ACTIF'
WHERE statut != 'ACTIF' AND statut != 'INACTIF' AND statut != 'SUSPENDU';

-- Ensure at least one ADMIN role exists
INSERT INTO utilisateurs (email, mot_de_passe_hash, nom, prenom, role, statut, email_verifie, accepte_cgu, consentement_donnees, consentement_contact, date_inscription) 
VALUES ('admin@isisu.fr', 'admin_hash', 'Admin', 'System', 'ADMIN', 'ACTIF', true, true, true, false, NOW())
ON CONFLICT (email) DO UPDATE SET role = 'ADMIN';

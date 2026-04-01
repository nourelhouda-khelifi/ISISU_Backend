-- ══════════════════════════════════════════════════════════════════════════
-- V10__insert_admin_user.sql
-- Insérer l'utilisateur ADMIN par défaut
--
-- Email: admin@isisu.fr
-- Mot de passe: Admin123! (hashé en BCrypt)
-- ══════════════════════════════════════════════════════════════════════════

INSERT INTO utilisateurs (
    email,
    mot_de_passe_hash,
    nom,
    prenom,
    role,
    statut,
    email_verifie,
    accepte_cgu,
    consentement_donnees,
    consentement_contact,
    date_verification_email,
    date_inscription
) VALUES (
    'admin@isisu.fr',
    -- ⚠️ IMPORTANT: Hash BCrypt de "Admin123!"
    -- Généré avec: BCryptPasswordEncoder(12).encode("Admin123!")
    -- Remplace par ton propre hash si nécessaire
    '$2a$12$S7YfWX5r.LKmE0J5RX2aFeRVCjKZlzQvvJpAR0HeHdtdW3Ej5pMqC',
    'Admin',
    'System',
    'ADMIN',
    'ACTIF',
    true,
    true,
    true,
    true,
    NOW(),
    NOW()
) ON CONFLICT (email) DO NOTHING;

-- ═══════════════════════════════════════════════════════════════════════════
-- ✅ Admin user inserted with credentials:
--    Email: admin@isisu.fr
--    Password: Admin123!
-- ═══════════════════════════════════════════════════════════════════════════

-- V6__create_otp_codes.sql
-- Table pour stockage des codes OTP (One Time Password)

CREATE TABLE IF NOT EXISTS otp_codes (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    code_hash VARCHAR(255) NOT NULL,
    expire_at TIMESTAMP NOT NULL,
    utilise BOOLEAN NOT NULL DEFAULT false,
    tentatives INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    last_tried_at TIMESTAMP,
    
    CONSTRAINT fk_otp_email FOREIGN KEY (email) REFERENCES utilisateurs(email) ON DELETE CASCADE
);

-- Index pour recherche rapide par email
CREATE INDEX idx_otp_codes_email ON otp_codes(email);
CREATE INDEX idx_otp_codes_utilise ON otp_codes(utilise);

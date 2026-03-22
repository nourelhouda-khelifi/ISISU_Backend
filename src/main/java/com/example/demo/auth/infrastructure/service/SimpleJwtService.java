package com.example.demo.auth.infrastructure.service;

import com.example.demo.auth.domain.Utilisateur;
import com.example.demo.common.config.JwtConfig;
import com.example.demo.common.config.JwtUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class SimpleJwtService implements JwtService {

    private final JwtConfig jwtConfig;

    @Override
    public String generateAccessToken(Utilisateur utilisateur) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(jwtConfig.getAccessTokenTtlSeconds());

        return Jwts.builder()
                .subject(String.valueOf(utilisateur.getId()))
                .issuer(jwtConfig.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .claim("email", utilisateur.getEmail())
                .claim("role", utilisateur.getRole().name())
                .signWith(signingKey())
                .compact();
    }

    @Override
    public JwtUser parseAndValidateAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Long userId = Long.valueOf(claims.getSubject());
            String email = claims.get("email", String.class);
            String role = claims.get("role", String.class);

            return new JwtUser(userId, email, role);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new IllegalStateException("Token JWT invalide ou expire", ex);
        }
    }

    @Override
    public long getAccessTokenTtlSeconds() {
        return jwtConfig.getAccessTokenTtlSeconds();
    }

    private SecretKey signingKey() {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(digest);
        } catch (Exception ex) {
            throw new IllegalStateException("Impossible d'initialiser la cle JWT", ex);
        }
    }
}

package com.example.demo.auth.infrastructure;

import com.example.demo.auth.domain.Utilisateur;
import com.example.demo.auth.infrastructure.repository.UtilisateurRepository;
import com.example.demo.common.config.JwtUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Facade pour accéder à l'utilisateur authentifié courant
 * Extrait l'utilisateur depuis le token JWT et le récupère de la base de données
 */
@Component
@RequiredArgsConstructor
public class AuthenticationFacade {

    private final UtilisateurRepository utilisateurRepository;

    /**
     * Récupère l'utilisateur actuellement authentifié depuis le contexte Spring Security
     * Convertit le JwtUser en entité Utilisateur depuis la base de données
     * @return L'utilisateur connecté
     * @throws RuntimeException si pas d'utilisateur connecté ou non trouvé en BD
     */
    public Utilisateur getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Utilisateur non authentifié");
        }
        
        Object principal = authentication.getPrincipal();
        
        // Vérifier que c'est un JwtUser
        if (!(principal instanceof JwtUser)) {
            throw new RuntimeException("Principal ne peut pas être converti en JwtUser: " + 
                    (principal != null ? principal.getClass() : "null"));
        }
        
        final JwtUser jwtUser = (JwtUser) principal;
        
        // Récupérer l'utilisateur depuis la base de données via son ID
        return utilisateurRepository.findById(jwtUser.userId())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + jwtUser.userId()));
    }

    /**
     * Retourne le nom d'utilisateur/email de l'utilisateur connecté
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JwtUser jwtUser) {
            return jwtUser.email();
        }
        return null;
    }

    /**
     * Extrait le userId du token JWT
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JwtUser jwtUser) {
            return jwtUser.userId();
        }
        throw new RuntimeException("Impossible d'extraire l'ID utilisateur du token");
    }

    /**
     * Vérifie si l'utilisateur est authentifié
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}

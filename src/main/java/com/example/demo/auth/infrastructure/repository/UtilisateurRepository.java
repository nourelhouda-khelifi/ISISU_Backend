package com.example.demo.auth.infrastructure.repository;

import com.example.demo.auth.domain.Utilisateur;
import com.example.demo.auth.domain.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCodeINE(String codeINE);
    
    /**
     * Compter le nombre d'utilisateurs par rôle
     */
    long countByRole(Role role);
}

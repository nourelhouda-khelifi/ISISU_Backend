package com.example.demo.dashboard.dto;

import com.example.demo.auth.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Infos basiques de l'utilisateur
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    
    private Long id;
    private String nom;
    private String prenom;
    private Role role;
}

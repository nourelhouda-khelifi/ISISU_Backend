package com.example.demo.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO pour un choix
 */
@Data
@AllArgsConstructor
@Builder
public class ChoixDTO {
    private Long id;
    private String libelle;
}

package com.example.demo.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DTO pour soumettre une réponse
 */
@Data
@AllArgsConstructor
@Builder
public class SubmitAnswerRequest {
    private Long questionSessionId;
    private List<Long> choixIds;
    private String reponseTexte;
}

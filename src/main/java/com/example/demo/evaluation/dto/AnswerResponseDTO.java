package com.example.demo.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO pour réponse après soumission
 */
@Data
@AllArgsConstructor
@Builder
public class AnswerResponseDTO {
    private Boolean correct;
    private String feedback;
    private QuestionDisplayDTO nextQuestion;
    private Boolean sessionTerminated;
    private Long tempsRestantSecondes;
}

package com.example.demo.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionScorePointDTO {
    private Integer sessionNum;
    private Double score;
    private LocalDateTime date;
    private String status; // ACQUIS, A_RENFORCER, MAITRISE, LACUNE
}

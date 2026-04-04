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
public class StudentInfoDTO {
    private Long id;
    private String niveau;
    private String parcours;
    private Integer nbSessions;
    private LocalDateTime firstSessionDate;
    private LocalDateTime lastSessionDate;
}

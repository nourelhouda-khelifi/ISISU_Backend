package com.example.demo.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompetenceTimelineDTO {
    private String competenceName;
    private String moduleName;
    private Long moduleId;
    private String moduleCode;
    private List<SessionScorePointDTO> scores;
    private String trend; // MOMENTUM, PROGRESSION, STAGNATION, REGRESSION, NOT_ATTEMPTED
    private Double velocity; // Points per session
    private Double acceleration; // Change in velocity
    private List<String> blockingOtherModules;
    private Integer sessionCountAbove80;
    private Integer sessionCountBelow50;
}

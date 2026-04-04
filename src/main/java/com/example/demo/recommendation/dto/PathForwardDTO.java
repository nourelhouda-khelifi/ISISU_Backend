package com.example.demo.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PathForwardDTO {
    private String nextSessionFocus;
    private String sessionNplus1Focus;
    private String estimatedTimeline;
    private List<String> suggestedModuleSequence;
    private String overallStrategy;
}

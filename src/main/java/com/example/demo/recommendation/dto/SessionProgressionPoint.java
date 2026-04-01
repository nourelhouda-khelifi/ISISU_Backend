package com.example.demo.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Point de progression pour une session
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionProgressionPoint {
    
    private int sessionNum;
    private LocalDateTime date;
    private int scoreGlobal;
}

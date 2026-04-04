package com.example.demo.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * Nombre de sessions par jour
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailySessionCountDTO {
    
    private LocalDate date;
    private Long nombre;
}

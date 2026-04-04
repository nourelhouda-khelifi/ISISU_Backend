package com.example.demo.recommendation;

import com.example.demo.recommendation.dto.CrossSessionRecommendationDTO;
import com.example.demo.recommendation.service.CrossSessionRecommendationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("CrossSessionRecommendationService Integration Tests")
class CrossSessionRecommendationServiceIT {

    @Autowired
    private CrossSessionRecommendationService crossSessionService;

    @Test
    @DisplayName("Should not throw ClassCastException when aggregating Hibernate proxy Competences")
    void testGenerateCrossSessionRecommendations_WithMultipleSessions() {
        // Given: A user with existing sessions and competence scores
        Long testUserId = 6L;  // User from test data
        
        // When: Generating cross-session recommendations
        // Then: Should not throw ClassCastException: Competence$HibernateProxy cannot be cast to Comparable
        assertDoesNotThrow(() -> {
            CrossSessionRecommendationDTO result = crossSessionService.generateCrossSessionRecommendations(testUserId);
            assertNotNull(result, "Recommendations should not be null");
        }, "Should generate recommendations without ClassCastException");
        
        System.out.println("✅ TreeMap → HashMap fix works - no ClassCastException from line 102");
    }
}

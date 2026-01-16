package com.leagueofcoding.api.dto.problem;

import com.leagueofcoding.api.enums.Difficulty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * UpdateProblemRequest - DTO cho updating problems.
 *
 * @author dao-nguyenminh
 */
public record UpdateProblemRequest(

        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        String description,

        Difficulty difficulty,

        @Positive(message = "Time limit must be positive")
        Integer timeLimitMs,

        @Positive(message = "Memory limit must be positive")
        Integer memoryLimitMb,

        Long categoryId,

        Boolean isActive
) {
}
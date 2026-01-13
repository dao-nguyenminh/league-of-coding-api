package com.leagueofcoding.api.dto.problem;

import com.leagueofcoding.api.enums.Difficulty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * UpdateProblemRequest - DTO cho updating problems.
 *
 * @author dao-nguyenminh
 */
@Builder
public record UpdateProblemRequest(
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        String description,

        Difficulty difficulty,

        @Min(value = 100, message = "Time limit must be at least 100ms")
        @Max(value = 10000, message = "Time limit must not exceed 10000ms")
        Integer timeLimitMs,

        @Min(value = 64, message = "Memory limit must be at least 64MB")
        @Max(value = 1024, message = "Memory limit must not exceed 1024MB")
        Integer memoryLimitMb,

        @Positive(message = "Category ID must be positive")
        Long categoryId,

        Boolean isActive
) {
}
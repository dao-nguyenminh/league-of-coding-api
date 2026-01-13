package com.leagueofcoding.api.dto.problem;

import com.leagueofcoding.api.enums.Difficulty;
import jakarta.validation.constraints.*;

import java.util.List;

/**
 * CreateProblemRequest - DTO cho creating problems.
 *
 * @author dao-nguyenminh
 */
public record CreateProblemRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Difficulty is required")
        Difficulty difficulty,

        @Positive(message = "Time limit must be positive")
        Integer timeLimitMs,

        @Positive(message = "Memory limit must be positive")
        Integer memoryLimitMb,

        @NotNull(message = "Category ID is required")
        Long categoryId,

        List<TestCaseRequest> testCases
) {
}
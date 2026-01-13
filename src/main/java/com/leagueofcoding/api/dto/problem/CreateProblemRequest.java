package com.leagueofcoding.api.dto.problem;

import com.leagueofcoding.api.enums.Difficulty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.List;

/**
 * CreateProblemRequest - DTO cho creating new problems.
 *
 * @author dao-nguyenminh
 */
@Builder
public record CreateProblemRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Difficulty is required")
        Difficulty difficulty,

        @Min(value = 100, message = "Time limit must be at least 100ms")
        @Max(value = 10000, message = "Time limit must not exceed 10000ms")
        Integer timeLimitMs,

        @Min(value = 64, message = "Memory limit must be at least 64MB")
        @Max(value = 1024, message = "Memory limit must not exceed 1024MB")
        Integer memoryLimitMb,

        @NotNull(message = "Category ID is required")
        @Positive(message = "Category ID must be positive")
        Long categoryId,

        @Valid
        List<TestCaseRequest> testCases
) {
}
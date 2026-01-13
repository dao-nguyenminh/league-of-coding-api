package com.leagueofcoding.api.dto.problem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * TestCaseRequest - DTO cho test case requests.
 *
 * @author dao-nguyenminh
 */
@Builder
public record TestCaseRequest(
        @NotBlank(message = "Input is required")
        String input,

        @NotBlank(message = "Expected output is required")
        String expectedOutput,

        @NotNull(message = "Is sample flag is required")
        Boolean isSample,

        Integer orderIndex
) {
}
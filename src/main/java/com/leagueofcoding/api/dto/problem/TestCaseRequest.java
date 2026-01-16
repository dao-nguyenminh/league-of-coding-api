package com.leagueofcoding.api.dto.problem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * TestCaseRequest - DTO cho creating/updating test cases.
 *
 * @author dao-nguyenminh
 */
public record TestCaseRequest(

        @NotBlank(message = "Input is required")
        String input,

        @NotBlank(message = "Expected output is required")
        String expectedOutput,

        @NotNull(message = "isSample flag is required")
        Boolean isSample,

        Integer orderIndex
) {
}
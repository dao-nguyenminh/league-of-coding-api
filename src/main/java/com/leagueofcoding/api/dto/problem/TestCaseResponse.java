package com.leagueofcoding.api.dto.problem;

import com.leagueofcoding.api.entity.TestCase;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * TestCaseResponse - DTO cho test case responses.
 *
 * @author dao-nguyenminh
 */
@Builder
public record TestCaseResponse(
        Long id,
        String input,
        String expectedOutput,
        Boolean isSample,
        Integer orderIndex,
        LocalDateTime createdAt
) {

    public static TestCaseResponse from(TestCase testCase) {
        return TestCaseResponse.builder()
                .id(testCase.getId())
                .input(testCase.getInput())
                .expectedOutput(testCase.getExpectedOutput())
                .isSample(testCase.getIsSample())
                .orderIndex(testCase.getOrderIndex())
                .createdAt(testCase.getCreatedAt())
                .build();
    }
}
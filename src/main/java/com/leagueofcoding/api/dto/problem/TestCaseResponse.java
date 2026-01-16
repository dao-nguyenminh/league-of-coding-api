package com.leagueofcoding.api.dto.problem;

import com.leagueofcoding.api.entity.TestCase;

/**
 * TestCaseResponse - DTO cho test case info.
 *
 * @author dao-nguyenminh
 */
public record TestCaseResponse(
        Long id,
        String input,
        String expectedOutput,
        Boolean isSample,
        Integer orderIndex
) {
    public static TestCaseResponse from(TestCase testCase) {
        return new TestCaseResponse(
                testCase.getId(),
                testCase.getInput(),
                testCase.getExpectedOutput(),
                testCase.getIsSample(),
                testCase.getOrderIndex()
        );
    }
}
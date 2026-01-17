package com.leagueofcoding.api.enums;

/**
 * Code submission status enumeration.
 *
 * @author dao-nguyenminh
 */
public enum SubmissionStatus {
    /**
     * Waiting to be judged
     */
    PENDING,

    /**
     * All test cases passed
     */
    PASSED,

    /**
     * Some test cases failed
     */
    FAILED,

    /**
     * Compilation error or runtime error
     */
    ERROR
}
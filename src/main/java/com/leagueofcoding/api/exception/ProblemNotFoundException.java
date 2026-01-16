package com.leagueofcoding.api.exception;

/**
 * Exception thrown khi problem không tồn tại.
 *
 * @author dao-nguyenminh
 */
public class ProblemNotFoundException extends RuntimeException {
    public ProblemNotFoundException(String message) {
        super(message);
    }
}
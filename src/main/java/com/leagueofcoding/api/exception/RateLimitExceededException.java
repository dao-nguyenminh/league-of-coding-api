package com.leagueofcoding.api.exception;

import lombok.Getter;

/**
 * Exception thrown khi rate limit bị vượt quá.
 *
 * @author dao-nguyenminh
 */
@Getter
public class RateLimitExceededException extends RuntimeException {

    private final long retryAfterSeconds;

    public RateLimitExceededException(String message, long retryAfterSeconds) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
    }
}
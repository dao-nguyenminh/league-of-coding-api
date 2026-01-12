package com.leagueofcoding.api.exception;

/**
 * Exception thrown khi refresh token invalid, expired, hoặc revoked.
 *
 * @author dao-nguyenminh
 */
public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
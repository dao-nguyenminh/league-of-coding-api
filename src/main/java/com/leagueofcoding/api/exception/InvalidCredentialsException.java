package com.leagueofcoding.api.exception;


/**
 * Exception thrown khi credentials không hợp lệ (login fail).
 *
 * @author dao-nguyenminh
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}

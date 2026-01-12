package com.leagueofcoding.api.exception;

/**
 * Exception thrown khi user không tồn tại.
 *
 * @author dao-nguyenminh
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

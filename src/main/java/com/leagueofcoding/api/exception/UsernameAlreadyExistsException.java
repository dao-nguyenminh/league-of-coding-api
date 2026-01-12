package com.leagueofcoding.api.exception;

/**
 * Exception thrown khi username đã tồn tại trong database.
 *
 * @author dao-nguyenminh
 */
public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}

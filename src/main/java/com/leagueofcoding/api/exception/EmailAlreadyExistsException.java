package com.leagueofcoding.api.exception;

/**
 * Exception thrown khi email đã tồn tại trong database.
 *
 * @author dao-nguyenminh
 */
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}

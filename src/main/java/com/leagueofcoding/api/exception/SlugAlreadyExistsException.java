package com.leagueofcoding.api.exception;

/**
 * Exception thrown khi slug đã tồn tại.
 *
 * @author dao-nguyenminh
 */
public class SlugAlreadyExistsException extends RuntimeException {
    public SlugAlreadyExistsException(String message) {
        super(message);
    }
}
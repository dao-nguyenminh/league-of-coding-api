package com.leagueofcoding.api.exception;

/**
 * Exception thrown khi category không tồn tại.
 *
 * @author dao-nguyenminh
 */
public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
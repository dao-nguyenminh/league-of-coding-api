package com.leagueofcoding.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * SlugAlreadyExistsException - Exception khi slug đã tồn tại.
 *
 * @author dao-nguyenminh
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class SlugAlreadyExistsException extends RuntimeException {

    public SlugAlreadyExistsException(String message) {
        super(message);
    }
}
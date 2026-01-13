package com.leagueofcoding.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ProblemNotFoundException - Exception khi problem không tìm thấy.
 *
 * @author dao-nguyenminh
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProblemNotFoundException extends RuntimeException {

    public ProblemNotFoundException(String message) {
        super(message);
    }
}
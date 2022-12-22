package com.example.likelionfinalproject.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    DUPLICATE_USERNAME(HttpStatus.CONFLICT),
    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED);

    private final HttpStatus httpStatus;

    ErrorCode (HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
package com.example.likelionfinalproject.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    DUPLICATE_USERNAME(HttpStatus.CONFLICT), NOT_FOUND_USERNAME(HttpStatus.NOT_FOUND);

    private final HttpStatus httpStatus;

    ErrorCode (HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
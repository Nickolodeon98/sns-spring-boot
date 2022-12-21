package com.example.likelionfinalproject.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "아이디가 중복됩니다.");

    private final HttpStatus httpStatus;
    private final String errorMessage;

    ErrorCode (HttpStatus httpStatus, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }
}
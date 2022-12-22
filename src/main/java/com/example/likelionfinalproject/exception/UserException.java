package com.example.likelionfinalproject.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserException extends RuntimeException{

    private ErrorCode errorCode;
    private String message;
}

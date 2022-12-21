package com.example.likelionfinalproject.exception;

import com.example.likelionfinalproject.domain.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ControllerAdvice
public class SocialAppException {

    @ResponseBody
    @ExceptionHandler
    public ResponseEntity<?> userJoinExceptionHandler(UserJoinException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Response.fail(exception.getErrorCode()));
    }

}

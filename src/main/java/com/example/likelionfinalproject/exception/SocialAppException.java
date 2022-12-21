package com.example.likelionfinalproject.exception;

import com.example.likelionfinalproject.domain.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class SocialAppException {

    @ResponseBody
    @ExceptionHandler
    public ResponseEntity<?> userJoinExceptionHandler(UserJoinException exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getErrorCode(), exception.getMessage());
        return ResponseEntity.status(exception.getErrorCode().getHttpStatus())
                .body(Response.fail(errorResponse));
    }

}

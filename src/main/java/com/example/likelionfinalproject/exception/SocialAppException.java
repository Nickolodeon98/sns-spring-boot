package com.example.likelionfinalproject.exception;

import com.example.likelionfinalproject.domain.Response;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class SocialAppException {

    @ResponseBody
    @ExceptionHandler(UserException.class)
    public ResponseEntity<?> userJoinExceptionHandler(UserException exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getErrorCode(), exception.getMessage());
        return ResponseEntity.status(exception.getErrorCode().getHttpStatus())
                .body(Response.fail(errorResponse));
    }

    @ResponseBody
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> expiredTokenExceptionHandler(ExpiredJwtException exception) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_PERMISSION, ErrorCode.INVALID_PERMISSION.getMessage());
        return ResponseEntity.status(errorResponse.getErrorCode().getHttpStatus())
                .body(Response.fail(errorResponse));
    }
}

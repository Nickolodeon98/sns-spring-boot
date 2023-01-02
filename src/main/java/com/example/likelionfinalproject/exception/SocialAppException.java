package com.example.likelionfinalproject.exception;

import com.example.likelionfinalproject.domain.Response;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;

@ControllerAdvice
@Slf4j
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
        log.error("토큰 에러:{}", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_PERMISSION);
        return ResponseEntity.status(errorResponse.getErrorCode().getHttpStatus())
                .body(Response.fail(errorResponse));
    }

    @ResponseBody
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<?> sqlExceptionHandler(SQLException e) {
        log.error("DB 에러:{}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.DATABASE_ERROR);
        return ResponseEntity.status(errorResponse.getErrorCode().getHttpStatus())
                .body(Response.fail(errorResponse));
    }
}

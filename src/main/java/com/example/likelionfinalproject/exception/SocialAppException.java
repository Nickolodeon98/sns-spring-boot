package com.example.likelionfinalproject.exception;

import com.example.likelionfinalproject.domain.Response;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ControllerAdvice
public class SocialAppException {

    @ResponseBody
    public Response<?> userJoinExceptionHandler(UserJoinException exception) {
        return Response.fail(exception);
    }

}

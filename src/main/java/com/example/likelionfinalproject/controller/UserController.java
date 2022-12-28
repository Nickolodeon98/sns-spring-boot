package com.example.likelionfinalproject.controller;

import com.example.likelionfinalproject.domain.Response;
import com.example.likelionfinalproject.domain.dto.UserJoinRequest;
import com.example.likelionfinalproject.domain.dto.UserJoinResponse;
import com.example.likelionfinalproject.domain.dto.UserLoginRequest;
import com.example.likelionfinalproject.domain.dto.UserLoginResponse;
import com.example.likelionfinalproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody(required = false) UserJoinRequest userJoinRequest) {
        UserJoinResponse userJoinResponse = userService.register(userJoinRequest);

        return Response.success(userJoinResponse);
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody(required = false) UserLoginRequest userLoginRequest) {
        UserLoginResponse userLoginResponse = userService.verify(userLoginRequest);

        return Response.success(userLoginResponse);
    }
}

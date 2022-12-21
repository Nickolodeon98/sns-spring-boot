package com.example.likelionfinalproject.controller;

import com.example.likelionfinalproject.domain.dto.UserJoinRequest;
import com.example.likelionfinalproject.domain.dto.UserJoinResponse;
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
    public Response<UserJoinResponse> joinUser(UserJoinRequest userJoinRequest) {
        UserJoinResponse userJoinResponse = userService.registerUser(userJoinRequest);

        return Response.success(userJoinResponse);
    }


}

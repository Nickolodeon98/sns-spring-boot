package com.example.likelionfinalproject.controller;

import com.example.likelionfinalproject.domain.Response;
import com.example.likelionfinalproject.domain.dto.UserJoinResponse;
import com.example.likelionfinalproject.domain.dto.UserLoginResponse;
import com.example.likelionfinalproject.domain.dto.UserRequest;
import com.example.likelionfinalproject.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "회원 가입", description = "중복되는 아이디가 아니라면 비밀번호와 함께 입력하여 회원으로 가입할 수 있다.")
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody(required = false) UserRequest userJoinRequest) {
        UserJoinResponse userJoinResponse = userService.register(userJoinRequest);

        return Response.success(userJoinResponse);
    }
    @Operation(summary = "회원 가입", description = "아이디와 해당 아이디로 회원 가입 시 입력한 비밀번호가 등록되어 있으면 고유 토큰을 발급받는다.")
    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody(required = false) UserRequest userLoginRequest) {
        UserLoginResponse userLoginResponse = userService.verify(userLoginRequest);

        return Response.success(userLoginResponse);
    }
}

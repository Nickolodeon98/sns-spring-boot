package com.example.likelionfinalproject.controller;

import com.example.likelionfinalproject.domain.Response;
import com.example.likelionfinalproject.domain.dto.response.UserJoinResponse;
import com.example.likelionfinalproject.domain.dto.response.UserLoginResponse;
import com.example.likelionfinalproject.domain.dto.request.UserRequest;
import com.example.likelionfinalproject.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Api(tags="USER Endpoints")
@ApiOperation(value = "사용자 관련 API 의 컨트롤러")
public class UserController {

    private final UserService userService;
    @Operation(summary = "회원 가입", description = "중복되는 아이디가 아니라면 비밀번호와 함께 입력하여 회원으로 가입할 수 있다.")
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody(required = false) UserRequest userJoinRequest) {
        UserJoinResponse userJoinResponse = userService.register(userJoinRequest);

        return Response.success(userJoinResponse);
    }
    @Operation(summary = "로그인", description = "아이디와 해당 아이디로 회원 가입 시 입력한 비밀번호가 등록되어 있으면 고유 토큰을 발급받는다.")
    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody(required = false) UserRequest userLoginRequest) {
        UserLoginResponse userLoginResponse = userService.verify(userLoginRequest);

        return Response.success(userLoginResponse);
    }
}

package com.example.likelionfinalproject.controller;

import com.example.likelionfinalproject.domain.dto.UserJoinRequest;
import com.example.likelionfinalproject.domain.dto.UserJoinResponse;
import com.example.likelionfinalproject.domain.dto.UserLoginRequest;
import com.example.likelionfinalproject.domain.dto.UserLoginResponse;
import com.example.likelionfinalproject.exception.ErrorCode;
import com.example.likelionfinalproject.exception.UserJoinException;
import com.example.likelionfinalproject.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    UserJoinResponse userJoinResponse;
    UserJoinRequest userJoinRequest;

    UserLoginRequest userLoginRequest;
    UserLoginResponse userLoginResponse;

    final String joinUrl = "/api/v1/users/join";
    final String loginUrl = "/api/v1/users/login";

    @BeforeEach
    void setUp() {
        userJoinRequest = UserJoinRequest.builder()
                .userId("sjeon0730")
                .password("1q2w3e4r")
                .name("전승환")
                .build();

        userJoinResponse = UserJoinResponse.builder()
                .userId("sjeon0730")
                .message("회원가입에 성공했습니다.")
                .build();

        userLoginRequest = UserLoginRequest.builder().userId("sjeon0730").password("1q2w3e4r").build();
        userLoginResponse = UserLoginResponse.builder().token("123456789").build();
    }

    @Test
    @DisplayName("회원가입에 성공한다.")
    @WithMockUser
    void success_join() throws Exception {
        given(userService.registerUser(any())).willReturn(userJoinResponse);

        mockMvc.perform(post(joinUrl).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(any()))
                .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService).registerUser(any());
    }

    @Test
    @DisplayName("회원가입에 실패한다.")
    @WithMockUser
    void fail_join() throws Exception {
        UserJoinRequest duplicateUser = UserJoinRequest.builder().userId("sjeon0730").name("전승환").password("1q2w3e4r").build();

        given(userService.registerUser(any()))
                .willThrow(new UserJoinException(ErrorCode.DUPLICATE_USERNAME,
                        duplicateUser.getUserId() + "는 이미 존재하는 아이디입니다."));

        mockMvc.perform(post(joinUrl).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(any()))
                .with(csrf()))
                .andExpect(status().isConflict())
                .andDo(print());

        verify(userService).registerUser(any());
    }

    @Test
    @DisplayName("로그인에 성공한다.")
    @WithMockUser
    void success_login() throws Exception {
        given(userService.verifyUser(any())).willReturn(userLoginResponse);

        mockMvc.perform(post(loginUrl).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(any())).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.token").value("123456789"))
                .andDo(print());

        verify(userService).verifyUser(any());
    }

    @Test
    @DisplayName("로그인에 실패한다 - 회원가입 된 아이디 없음")
    @WithMockUser
    void fail_login_no_id() throws Exception {
        given(userService.verifyUser(any())).willThrow(new UserJoinException(ErrorCode.USERNAME_NOT_FOUND, "등록되지 않은 아이디입니다."));

        mockMvc.perform(post(loginUrl).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(any())).with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("USERNAME_NOT_FOUND"))
                .andExpect(jsonPath("$.result.message").value("등록되지 않은 아이디입니다."))
                .andDo(print());

        verify(userService).verifyUser(any());
    }

    @Test
    @DisplayName("로그인에 실패한다 - 비밀번호가 잘못됨")
    @WithMockUser
    void fail_login_wrong_password() throws Exception {
        given(userService.verifyUser(any())).willThrow(new UserJoinException(ErrorCode.INVALID_PASSWORD, "비밀번호가 잘못되었습니다."));

        mockMvc.perform(post(loginUrl).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(any())).with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("INVALID_PASSWORD"))
                .andExpect(jsonPath("$.result.message").value("비밀번호가 잘못되었습니다."))
                .andDo(print());

        verify(userService).verifyUser(any());
    }
}
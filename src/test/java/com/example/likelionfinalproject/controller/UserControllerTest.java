package com.example.likelionfinalproject.controller;

import com.example.likelionfinalproject.domain.dto.UserJoinRequest;
import com.example.likelionfinalproject.domain.dto.UserJoinResponse;
import com.example.likelionfinalproject.exception.UserJoinException;
import com.example.likelionfinalproject.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    String joinUrl;

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

        joinUrl = "api/v1/users/join";
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
    void fail_join() throws Exception {
        UserJoinRequest duplicateUser = UserJoinRequest.builder().userId("sjeon0730").name("전승환").password("1q2w3e4r").build();
        given(userService.registerUser(duplicateUser)).willThrow(new UserJoinException());

        mockMvc.perform(post(joinUrl).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(duplicateUser))
                .with(csrf()))
                .andExpect(status().isConflict())
                .andDo(print());

        verify(userService).registerUser(duplicateUser);
    }
}
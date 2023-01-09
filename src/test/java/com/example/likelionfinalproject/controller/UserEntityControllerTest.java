package com.example.likelionfinalproject.controller;

import com.example.likelionfinalproject.domain.dto.UserJoinResponse;
import com.example.likelionfinalproject.domain.dto.UserLoginResponse;
import com.example.likelionfinalproject.domain.dto.UserRequest;
import com.example.likelionfinalproject.enums.UserTestEssentials;
import com.example.likelionfinalproject.exception.ErrorCode;
import com.example.likelionfinalproject.exception.UserException;
import com.example.likelionfinalproject.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserEntityControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    UserJoinResponse userJoinResponse;
    UserRequest userRequest;
    UserLoginResponse userLoginResponse;
    final Integer userId = 1;
    final String joinUrl = UserTestEssentials.USER_URL.getValue() + "join";
    final String loginUrl = UserTestEssentials.USER_URL.getValue() + "login";
    @BeforeEach
    void setUp() {
        userRequest = UserRequest.builder()
                .userName(UserTestEssentials.USER_NAME.getValue())
                .password(UserTestEssentials.PASSWORD.getValue())
                .build();

        userJoinResponse = UserJoinResponse.builder()
                .userName(UserTestEssentials.USER_NAME.getValue())
                .userId(userId)
                .build();

        userLoginResponse = UserLoginResponse.builder().jwt(UserTestEssentials.TOKEN.getValue()).build();
    }

    private static Stream<Arguments> provideErrorCases() {
        return Stream.of(
                Arguments.of(Named.of("아이디가 없음", status().isNotFound()), ErrorCode.USERNAME_NOT_FOUND),
                Arguments.of(Named.of("비밀번호가 잘못됨", status().isUnauthorized()), ErrorCode.INVALID_PASSWORD));
    }

    @Nested
    @DisplayName("회원가입")
    class Joining {
        @Test
        @DisplayName("성공")
        @WithMockUser
        void success_join() throws Exception {
            given(userService.register(any())).willReturn(userJoinResponse);

            mockMvc.perform(post(joinUrl).contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(userRequest))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.userId").value(userId))
                    .andExpect(jsonPath("$.result.userName").value(UserTestEssentials.USER_NAME.getValue()))
                    .andDo(print());

            verify(userService).register(any());
        }

        @Test
        @DisplayName("실패")
        @WithMockUser
        void fail_join() throws Exception {
            // 아이디가 같고 비밀번호가 다르면 중복된 사용자이다.
            userRequest.setPassword(Double.toString(Math.random()));
            UserRequest duplicateUser = userRequest;

            given(userService.register(any()))
                    .willThrow(new UserException(ErrorCode.DUPLICATE_USERNAME,
                            duplicateUser.getUserName() + "는 이미 존재하는 아이디입니다."));

            mockMvc.perform(post(joinUrl).contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(duplicateUser))
                            .with(csrf()))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value(ErrorCode.DUPLICATE_USERNAME.name()))
                    .andExpect(jsonPath("$.result.message").value(duplicateUser.getUserName() + "는 이미 존재하는 아이디입니다."))
                    .andDo(print());

            verify(userService).register(any());
        }
    }

    @Nested
    @DisplayName("로그인")
    class LoggingIn {
        @Test
        @DisplayName("성공")
        @WithMockUser
        void success_login() throws Exception {
            given(userService.verify(any())).willReturn(userLoginResponse);

            mockMvc.perform(post(loginUrl).contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(any())).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.jwt").value(UserTestEssentials.TOKEN.getValue()))
                    .andDo(print());

            verify(userService).verify(any());
        }

        @ParameterizedTest
        @DisplayName("실패")
        @WithMockUser
        @MethodSource("com.example.likelionfinalproject.controller.UserControllerTest#provideErrorCases")
        void fail_login(ResultMatcher error, ErrorCode code) throws Exception {
            given(userService.verify(any())).willThrow(new UserException(code, code.getMessage()));

            mockMvc.perform(post(loginUrl).contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(userRequest)).with(csrf()))
                    .andExpect(error)
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value(code.name()))
                    .andExpect(jsonPath("$.result.message").value(code.getMessage()))
                    .andDo(print());

            verify(userService).verify(any());
        }
    }
}
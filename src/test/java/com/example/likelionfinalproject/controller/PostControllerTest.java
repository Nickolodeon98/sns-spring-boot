package com.example.likelionfinalproject.controller;

import com.example.likelionfinalproject.domain.dto.PostRequest;
import com.example.likelionfinalproject.domain.dto.PostResponse;
import com.example.likelionfinalproject.exception.ErrorCode;
import com.example.likelionfinalproject.exception.UserException;
import com.example.likelionfinalproject.service.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostService postService;

    @Autowired
    ObjectMapper objectMapper;

    PostRequest postRequest;

    PostResponse postResponse;

    String postUrl;

    @BeforeEach
    void setUp() {
        postRequest = PostRequest.builder()
                .title("포스트 제목")
                .body("포스트 내용")
                .build();

        postResponse = PostResponse.builder()
                .message("포스트 등록 완료")
                .postId(1L)
                .build();

        postUrl = "/api/v1/posts";

    }

    @Test
    @DisplayName("포스트 작성에 성공한다")
    @WithMockUser
    public void post_success() throws Exception {

        given(postService.createNewPost(any(), any())).willReturn(postResponse);

        mockMvc.perform(post(postUrl).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(postRequest)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").value("포스트 등록 완료"))
                .andDo(print());

        verify(postService).createNewPost(any(), any());
    }

    @Test
    @DisplayName("포스트 작성에 실패한다")
    @WithMockUser
    public void post_fail() throws Exception {
        given(postService.createNewPost(any(), any()))
                .willThrow(new UserException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다."));

        mockMvc.perform(post(postUrl).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(any()))
                .content(objectMapper.writeValueAsBytes(any())).with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("INVALID_PERMISSION"))
                .andExpect(jsonPath("$.result.message").value("사용자가 권한이 없습니다."))
                .andDo(print());

        verify(postService).createNewPost(any(), any());
    }
}
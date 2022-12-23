package com.example.likelionfinalproject.controller;

import com.example.likelionfinalproject.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
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
                .author("포스트 작성자")
                .createdAt("포스트 작성 날짜")
                .build();

        postResponse = PostResponse.builder()
                .message("포스트 등록 완료")
                .postId(1)
                .build();

        postUrl = "/api/v1/posts";

    }

    @Test
    @DisplayName("포스트 작성에 성공한다")
    public void post_success() {

        given(postService.createNewPost(postRequest)).willReturn(postResponse);

        mockMvc.perform(post(postUrl).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(postRequest)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").value("포스트 등록 완료"))
                .andDo(print());

        verify(postService).createNewPost(postRequest);
    }

}
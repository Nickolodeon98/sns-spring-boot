package com.example.likelionfinalproject.controller;

import com.example.likelionfinalproject.domain.dto.PostRequest;
import com.example.likelionfinalproject.domain.dto.PostResponse;
import com.example.likelionfinalproject.domain.dto.SelectedPostResponse;
import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.exception.ErrorCode;
import com.example.likelionfinalproject.exception.UserException;
import com.example.likelionfinalproject.service.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.sql.Date;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    SelectedPostResponse selectedPostResponse;

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

        selectedPostResponse = SelectedPostResponse.builder()
                .id(1L)
                .title("title")
                .body("body")
                .userName("username")
                .createdAt(LocalDateTime.of(2022, 12, 26, 18, 03, 14))
                .lastModifiedAt(LocalDateTime.of(2022, 12, 26, 18, 03, 14))
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

    @Test
    @DisplayName("주어진 고유 번호로 포스트를 조회한다")
    @WithMockUser
    public void find_post() throws Exception {
        Long postsId = 1L;

        given(postService.acquireSinglePost(postsId)).willReturn(selectedPostResponse);

        String selectUrl = String.format("%s/%d", postUrl, postsId);

        mockMvc.perform(get(selectUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").value(1L))
                .andExpect(jsonPath("$.result.title").value("title"))
                .andExpect(jsonPath("$.result.body").value("body"))
                .andExpect(jsonPath("$.result.userName").value("username"))
                .andDo(print());

        verify(postService).acquireSinglePost(postsId);
    }

    @Captor
    ArgumentCaptor<Page<Post>> postArgumentCaptor;

    @Test
    @DisplayName("등록된 모든 포스트를 조회한다.")
    @WithMockUser
    void find_every_posts() throws Exception {
        final int size = 20;
        Pageable pageable = PageRequest.of(1, size, Sort.by("id").descending());

        List<SelectedPostResponse> multiplePosts = List.of(selectedPostResponse);

        Page<SelectedPostResponse> posts = new PageImpl<>(multiplePosts);

        given(postService.listAllPosts(pageable)).willReturn(posts);

        mockMvc.perform(get(postUrl))
                .andExpect(status().isAccepted())
                .andDo(print());

        verify(postArgumentCaptor.capture()).stream()
                .map(i->SelectedPostResponse.of(mock(Post.class))).collect(Collectors.toList());

        Page<Post> createdPost = postArgumentCaptor.getValue();

        Assertions.assertEquals(pageable.getPageSize(), createdPost.getSize());
        Assertions.assertEquals(pageable.getSort(), createdPost.getSort());
        Assertions.assertEquals(pageable.getPageNumber(), createdPost.getTotalPages());
    }
}
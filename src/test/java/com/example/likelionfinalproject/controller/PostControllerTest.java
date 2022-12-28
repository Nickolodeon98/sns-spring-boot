package com.example.likelionfinalproject.controller;

import com.example.likelionfinalproject.domain.dto.EditPostRequest;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpServerErrorException;

import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.sql.Date;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    Integer postId;
    String postUrl;
    String editUrl;
    String deleteUrl;
    EditPostRequest editPostRequest;
    PostResponse editedPost;
    PostResponse deletedPostResponse;
    @BeforeEach
    void setUp() {
        postId = 1;

        postRequest = PostRequest.builder()
                .title("포스트 제목")
                .body("포스트 내용")
                .build();

        postResponse = PostResponse.builder()
                .message("포스트 등록 완료")
                .postId(postId)
                .build();

        selectedPostResponse = SelectedPostResponse.builder()
                .id(postId)
                .title("title")
                .body("body")
                .userName("username")
                .createdAt(LocalDateTime.of(2022, 12, 26, 18, 03, 14))
                .lastModifiedAt(LocalDateTime.of(2022, 12, 26, 18, 03, 14))
                .build();

        postUrl = "/api/v1/posts";

        editPostRequest = EditPostRequest.builder()
                .title("title")
                .body("body")
                .build();

        editedPost = PostResponse.builder()
                .message("포스트 수정 완료")
                .postId(postId)
                .build();

        deletedPostResponse = PostResponse.builder()
                .message("포스트 삭제 완료")
                .postId(postId)
                .build();

        editUrl = String.format("%s/%d", postUrl, postId);
        deleteUrl = String.format("%s/%d", postUrl, postId);


    }

    @Test
    @DisplayName("포스트 작성에 성공한다")
    @WithMockUser
    public void post_success() throws Exception {

        given(postService.createPost(any(), any())).willReturn(postResponse);

        mockMvc.perform(post(postUrl).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").value("포스트 등록 완료"))
                .andDo(print());

        verify(postService).createPost(any(), any());
    }

    @Test
    @DisplayName("포스트 작성에 실패한다")
    @WithMockUser
    public void post_fail() throws Exception {
        given(postService.createPost(any(), any()))
                .willThrow(new UserException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다."));

        mockMvc.perform(post(postUrl).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(any()))
                        .content(objectMapper.writeValueAsBytes(any())).with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("INVALID_PERMISSION"))
                .andExpect(jsonPath("$.result.message").value("사용자가 권한이 없습니다."))
                .andDo(print());

//        verify(postService).createNewPost(any(), any());
    }

    @Test
    @DisplayName("주어진 고유 번호로 포스트를 조회한다")
    @WithMockUser
    public void find_post() throws Exception {
        given(postService.acquirePost(postId)).willReturn(selectedPostResponse);

        String selectUrl = String.format("%s/%d", postUrl, postId);

        mockMvc.perform(get(selectUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").value(1L))
                .andExpect(jsonPath("$.result.title").value("title"))
                .andExpect(jsonPath("$.result.body").value("body"))
                .andExpect(jsonPath("$.result.userName").value("username"))
                .andDo(print());

        verify(postService).acquirePost(postId);
    }

    @Captor
    ArgumentCaptor<Pageable> postArgumentCaptor;

    @Test
    @DisplayName("등록된 모든 포스트를 조회한다.")
    @WithMockUser
    void find_every_posts() throws Exception {
        final int size = 20;
        Pageable pageable = PageRequest.of(0, size, Sort.by("id").descending());

        List<SelectedPostResponse> multiplePosts = List.of(selectedPostResponse);

        Page<SelectedPostResponse> posts = new PageImpl<>(multiplePosts);

        given(postService.listAllPosts(pageable)).willReturn(posts);

        mockMvc.perform(get(postUrl).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(postService).listAllPosts(postArgumentCaptor.capture());

        Pageable createdPost = postArgumentCaptor.getValue();

        Assertions.assertEquals(pageable.getPageSize(), createdPost.getPageSize());
        Assertions.assertEquals(pageable.getSort(), createdPost.getSort());
        Assertions.assertEquals(pageable.getPageNumber(), createdPost.getPageNumber());
    }

    @Test
    @DisplayName("고유 아이디로 찾은 특정 포스트의 내용 수정에 성공한다.")
    @WithMockUser
    void success_edit_post() throws Exception {

        given(postService.editPost(any(), eq(postId), any())).willReturn(editedPost);

        mockMvc.perform(put(editUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(editPostRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").value("포스트 수정 완료"))
                .andExpect(jsonPath("$.result.postId").value(postId))
                .andDo(print());


        verify(postService).editPost(any(), eq(postId), any());
    }

    @Test
    @DisplayName("로그인 하지 않았거나 작성자와 현재 로그인된 유저가 일치하지 않아 포스트 수정에 실패한다.")
    @WithMockUser
    void fail_edit_post_inconsistent_user() throws Exception {

        given(postService.editPost(any(), eq(postId), any()))
                .willThrow(new UserException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다."));

        mockMvc.perform(put(editUrl).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(editPostRequest))
                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.result.errorCode").value("INVALID_PERMISSION"))
                .andDo(print());

        verify(postService).editPost(any(), eq(postId), any());
    }

    @Test
    @DisplayName("데이터베이스에서 수정할 포스트를 찾지 못하면 수정에 실패한다.")
    @WithMockUser
    void fail_edit_post_not_in_db() throws Exception {

        given(postService.editPost(any(), eq(postId), any()))
                .willThrow(new UserException(ErrorCode.DATABASE_ERROR, "DB 에러"));

        mockMvc.perform(put(editUrl).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(editPostRequest))
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andDo(print());

        verify(postService).editPost(any(), eq(postId), any());

    }

    @Test
    @DisplayName("주어진 고유 번호를 갖는 포스트 삭제에 성공한다.")
    @WithMockUser
    void success_delete_post() throws Exception {

        given(postService.removePost(eq(postId), any())).willReturn(deletedPostResponse);

        mockMvc.perform(delete(deleteUrl).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").value("포스트 삭제 완료"))
                .andExpect(jsonPath("$.result.postId").value(postId))
                .andDo(print());

        verify(postService).removePost(eq(postId), any());
    }

    @Test
    @DisplayName("인증되지 않은 사용자가 포스트를 삭제하면 실패한다.")
    void fail_delete_post_unauthorized() throws Exception {
        given(postService.removePost(eq(postId), any()))
                .willThrow(new UserException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다."));

        mockMvc.perform(delete(deleteUrl).with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

//    @Test
//    @DisplayName("로그인된 사용자와 삭제하려는 포스트의 작성자가 다르면 삭제에 실패한다.")
//    @WithMockUser
//    void fail_delete_post_inconsistent_author() throws Exception {
//        given(postService.removePost(eq(postId), any()))
//                .willThrow(new UserException(ErrorCode.USERNAME_NOT_FOUND, "Not Found."));
//
//        mockMvc.perform(delete(deleteUrl).with(csrf()))
//                .andExpect(status().isNotFound())
//                .andDo(print());
//    }

    @ParameterizedTest
    @DisplayName("DB 에 오류가 나면 포스트 삭제를 실패한다.")
    @WithMockUser
    @MethodSource("provideDeleteErrorCase")
    void fail_delete_post(ErrorCode errorCode, ResultMatcher isError) throws Exception {
        given(postService.removePost(eq(postId), any()))
                .willThrow(new UserException(errorCode, errorCode.getMessage()));

        mockMvc.perform(delete(deleteUrl).with(csrf()))
                .andExpect(isError)
                .andDo(print());
    }

    private static Stream<Arguments> provideDeleteErrorCase() {
        return Stream.of(Arguments.of(ErrorCode.USERNAME_NOT_FOUND, status().isInternalServerError()));
    }

}
package com.example.likelionfinalproject.controller;

import com.example.likelionfinalproject.domain.dto.PostRequest;
import com.example.likelionfinalproject.domain.dto.PostResponse;
import com.example.likelionfinalproject.domain.dto.SelectedPostResponse;
import com.example.likelionfinalproject.exception.ErrorCode;
import com.example.likelionfinalproject.exception.UserException;
import com.example.likelionfinalproject.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

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
    PostRequest editPostRequest;
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

        editPostRequest = PostRequest.builder()
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

    private static Stream<Arguments> provideErrorCase() {
        return Stream.of(Arguments.of(Named.of("작성자와 사용자 불일치", status().isNotFound()),
                        ErrorCode.USERNAME_NOT_FOUND),
                Arguments.of(Named.of("사용자 인증 실패", status().isUnauthorized()),
                        ErrorCode.INVALID_PERMISSION),
                Arguments.of(Named.of("DB 오류", status().isInternalServerError()),
                        ErrorCode.DATABASE_ERROR));
    }
    
    @Nested
    @DisplayName("포스트 작성")
    class PostAddition {

        @Test
        @DisplayName("포스트 작성 성공")
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
        @DisplayName("포스트 작성 실패")
        @WithMockUser
        public void post_fail() throws Exception {
            given(postService.createPost(any(), any()))
                    .willThrow(new UserException(ErrorCode.INVALID_PERMISSION, ErrorCode.INVALID_PERMISSION.getMessage()));

            mockMvc.perform(post(postUrl).contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(any()))
                            .content(objectMapper.writeValueAsBytes(any())).with(csrf()))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value(ErrorCode.INVALID_PERMISSION.name()))
                    .andExpect(jsonPath("$.result.message").value(ErrorCode.INVALID_PERMISSION.getMessage()))
                    .andDo(print());

            verify(postService).createPost(any(), any());
        }
    }


    @Nested
    @DisplayName("포스트 조회")
    class PostAcquisition {
        @Test
        @DisplayName("성공 - 단건")
        @WithMockUser
        public void find_post() throws Exception {
            given(postService.acquirePost(postId)).willReturn(selectedPostResponse);

            String selectUrl = String.format("%s/%d", postUrl, postId);

            mockMvc.perform(get(selectUrl))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.id").value(1))
                    .andExpect(jsonPath("$.result.title").value("title"))
                    .andExpect(jsonPath("$.result.body").value("body"))
                    .andExpect(jsonPath("$.result.userName").value("username"))
                    .andDo(print());

            verify(postService).acquirePost(postId);
        }

        @Captor
        ArgumentCaptor<Pageable> postArgumentCaptor;

        @Test
        @DisplayName("성공 - 모든 포스트")
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
    }

    @Nested
    @DisplayName("포스트 수정")
    class PostEdition {

        @Test
        @DisplayName("포스트 수정 성공")
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

        @ParameterizedTest
        @DisplayName("포스트 수정 실패")
        @WithMockUser
        @MethodSource("com.example.likelionfinalproject.controller.PostControllerTest#provideErrorCase")
        void fail_edit_post(ResultMatcher error, ErrorCode code) throws Exception {

            given(postService.editPost(any(), eq(postId), any()))
                    .willThrow(new UserException(code, code.getMessage()));

            mockMvc.perform(put(editUrl).contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(editPostRequest))
                            .with(csrf()))
                    .andExpect(error)
                    .andExpect(jsonPath("$.result.errorCode").value(code.name()))
                    .andDo(print());

            verify(postService).editPost(any(), eq(postId), any());
        }
    }

    @Test
    @DisplayName("포스트 삭제 성공")
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

    @ParameterizedTest
    @DisplayName("포스트 삭제 실패")
    @WithMockUser
    @MethodSource("provideErrorCase")
    void fail_delete_post(ResultMatcher error, ErrorCode code) throws Exception {
        given(postService.removePost(eq(postId), any()))
                .willThrow(new UserException(code, code.getMessage()));

        mockMvc.perform(delete(deleteUrl).with(csrf()))
                .andExpect(error)
                .andDo(print());
    }

}
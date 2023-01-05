package com.example.likelionfinalproject.controller;

import com.example.likelionfinalproject.domain.Response;
import com.example.likelionfinalproject.domain.dto.*;
import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.enums.PostTestEssentials;
import com.example.likelionfinalproject.exception.ErrorCode;
import com.example.likelionfinalproject.exception.UserException;
import com.example.likelionfinalproject.fixture.PostFixture;
import com.example.likelionfinalproject.service.CommentService;
import com.example.likelionfinalproject.service.LikeService;
import com.example.likelionfinalproject.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

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

    @MockBean
    CommentService commentService;

    @MockBean
    LikeService likeService;

    @Autowired
    ObjectMapper objectMapper;

    PostRequest postRequest;

    SelectedPostResponse selectedPostResponse;
    final String url = "/api/v1/posts/1";
    final String userName = "username";
    final Integer postId = 1;
    final LocalDateTime timeInfo = LocalDateTime.of(2022, 12, 26, 18, 03, 14);
    CommentRequest commentRequest;
    CommentResponse commentResponse;
    final Integer commentId = 1;
    Page<SelectedPostResponse> posts;
    Pageable pageable;
    final int size = 20;

    @BeforeEach
    void setUp() {
        postRequest = PostRequest.builder()
                .title(PostTestEssentials.POST_TITLE.getValue())
                .body(PostTestEssentials.POST_BODY.getValue())
                .build();

        selectedPostResponse = SelectedPostResponse.builder()
                .id(postId)
                .title(PostTestEssentials.POST_TITLE.getValue())
                .body(PostTestEssentials.POST_BODY.getValue())
                .userName(userName)
                .createdAt(timeInfo)
                .lastModifiedAt(timeInfo)
                .build();

        commentRequest = CommentRequest.builder().comment("comment test").build();

        commentResponse = CommentResponse.builder()
                .id(commentId).comment("comment test").userName(userName).postId(postId)
                .createdAt(timeInfo)
                .build();

        pageable = PageRequest.of(0, size, Sort.by("createdAt").descending());

        posts = new PageImpl<>(List.of(selectedPostResponse));
    }

    private static Stream<Arguments> provideErrorCase() {
        return Stream.of(Arguments.of(Named.of("작성자와 사용자 불일치", status().isNotFound()),
                        ErrorCode.USERNAME_NOT_FOUND),
                Arguments.of(Named.of("사용자 인증 실패", status().isUnauthorized()),
                        ErrorCode.INVALID_PERMISSION),
                Arguments.of(Named.of("포스트 없음", status().isNotFound()),
                        ErrorCode.POST_NOT_FOUND),
                Arguments.of(Named.of("DB 오류", status().isInternalServerError()),
                        ErrorCode.DATABASE_ERROR));
    }
    
    @Nested
    @DisplayName("포스트 작성")
    class PostAddition {

        @Test
        @DisplayName("성공")
        @WithMockUser
        public void post_success() throws Exception {

            given(postService.createPost(any(), any()))
                    .willReturn(PostResponse.of(PostFixture.get(postId), PostTestEssentials.POST_CREATE_MESSAGE.getValue()));

            mockMvc.perform(post(PostTestEssentials.POST_URL.getValue()).contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(postRequest)).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.message").value(PostTestEssentials.POST_CREATE_MESSAGE.getValue()))
                    .andDo(print());

            verify(postService).createPost(any(), any());
        }

        @Test
        @DisplayName("실패")
        @WithMockUser
        public void post_fail() throws Exception {
            given(postService.createPost(any(), any()))
                    .willThrow(new UserException(ErrorCode.INVALID_PERMISSION, ErrorCode.INVALID_PERMISSION.getMessage()));

            mockMvc.perform(post(PostTestEssentials.POST_URL.getValue()).contentType(MediaType.APPLICATION_JSON)
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
    @Captor
    ArgumentCaptor<Pageable> postArgumentCaptor;

    @Nested
    @DisplayName("포스트 조회")
    class PostAcquisition {
        @Test
        @DisplayName("성공 - 단건")
        @WithMockUser
        public void find_post() throws Exception {
            given(postService.acquirePost(postId)).willReturn(selectedPostResponse);


            mockMvc.perform(get(url))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.id").value(selectedPostResponse.getId()))
                    .andExpect(jsonPath("$.result.title").value(selectedPostResponse.getTitle()))
                    .andExpect(jsonPath("$.result.body").value(selectedPostResponse.getBody()))
                    .andExpect(jsonPath("$.result.userName").value(selectedPostResponse.getUserName()))
                    .andDo(print());

            verify(postService).acquirePost(postId);
        }

        @Test
        @DisplayName("성공 - 모든 포스트")
        @WithMockUser
        void find_every_posts() throws Exception {
            given(postService.listAllPosts(pageable)).willReturn(posts);

            mockMvc.perform(get(PostTestEssentials.POST_URL.getValue()).with(csrf()))
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
        @DisplayName("성공")
        @WithMockUser
        void success_edit_post() throws Exception {

            given(postService.editPost(any(), eq(postId), any()))
                    .willReturn(PostResponse.of(PostFixture.get(postId), PostTestEssentials.POST_EDIT_MESSAGE.getValue()));

            mockMvc.perform(put(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(postRequest))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.message").value(PostTestEssentials.POST_EDIT_MESSAGE.getValue()))
                    .andExpect(jsonPath("$.result.postId").value(postId))
                    .andDo(print());


            verify(postService).editPost(any(), eq(postId), any());
        }

        @ParameterizedTest
        @DisplayName("실패")
        @WithMockUser
        @MethodSource("com.example.likelionfinalproject.controller.PostControllerTest#provideErrorCase")
        void fail_edit_post(ResultMatcher error, ErrorCode code) throws Exception {

            given(postService.editPost(any(), eq(postId), any()))
                    .willThrow(new UserException(code, code.getMessage()));

            mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(postRequest))
                            .with(csrf()))
                    .andExpect(error)
                    .andExpect(jsonPath("$.result.errorCode").value(code.name()))
                    .andDo(print());

            verify(postService).editPost(any(), eq(postId), any());
        }
    }


    @Nested
    @DisplayName("포스트 삭제")
    class PostRemoval {
        @Test
        @DisplayName("성공")
        @WithMockUser
        void success_delete_post() throws Exception {

            given(postService.removePost(eq(postId), any()))
                    .willReturn(PostResponse.of(PostFixture.get(postId), PostTestEssentials.POST_DELETE_MESSAGE.getValue()));

            mockMvc.perform(delete(url).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.message").value(PostTestEssentials.POST_DELETE_MESSAGE.getValue()))
                    .andExpect(jsonPath("$.result.postId").value(postId))
                    .andDo(print());

            verify(postService).removePost(eq(postId), any());
        }

        @ParameterizedTest
        @DisplayName("실패")
        @WithMockUser
        @MethodSource("com.example.likelionfinalproject.controller.PostControllerTest#provideErrorCase")
        void fail_delete_post(ResultMatcher error, ErrorCode code) throws Exception {
            given(postService.removePost(eq(postId), any()))
                    .willThrow(new UserException(code, code.getMessage()));

            mockMvc.perform(delete(url).with(csrf()))
                    .andExpect(error)
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("댓글 작성")
    class CommentAddition {
        /* 리팩토링 필요함 */
        @Test
        @DisplayName("성공")
        @WithMockUser
        void success_add_comment() throws Exception {
            given(commentService.uploadComment(any(), any(), eq(1))).willReturn(commentResponse);

            mockMvc.perform(post(url + "/comments")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(commentRequest)).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.id").value(1))
                    .andExpect(jsonPath("$.result.comment").value("comment test"))
                    .andExpect(jsonPath("$.result.userName").exists())
                    .andExpect(jsonPath("$.result.postId").exists())
                    .andExpect(jsonPath("$.result.createdAt").exists())
                    .andDo(print());

            verify(commentService).uploadComment(any(), any(), eq(1));
        }

        @Test
        @DisplayName("실패")
        @WithMockUser
        void fail_add_comment_no_post() throws Exception {
            given(commentService.uploadComment(any(), any(), eq(1)))
                    .willThrow(new UserException(ErrorCode.POST_NOT_FOUND, ErrorCode.POST_NOT_FOUND.getMessage()));

            mockMvc.perform(post(url + "/comments").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(commentRequest)).with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value(ErrorCode.POST_NOT_FOUND.name()))
                    .andExpect(jsonPath("$.result.message").value(ErrorCode.POST_NOT_FOUND.getMessage()))
                    .andDo(print());

            verify(commentService).uploadComment(any(), any(), eq(1));
        }


        @Test
        @DisplayName("실패 - 로그인하지 않음")
        @WithMockUser
        void fail_add_comment_not_a_user() throws Exception {
            /* 사실 서비스 단에서 에러가 던져지는 것은 아니기 때문에, 불완전하게 테스트한다고 할 수 있다.
             * 만일 mock user 가 아니라 anonymous user 로 테스트 해보면 어떨까?
             * anonymous user 로 테스트한 결과: 상태 코드는 401 unauthorized 가 나오지만, json 형태로 에러를 반환하지는 않는다.
             * 컨트롤러의 반환 값으로 인식하지 않아서 그런 듯 하다. */
            given(commentService.uploadComment(any(), any(), eq(1)))
//                    .willReturn(commentResponse);
                    .willThrow(new UserException(ErrorCode.INVALID_PERMISSION, ErrorCode.INVALID_PERMISSION.getMessage()));

            mockMvc.perform(post(url + "/comments")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(commentRequest)).with(csrf()))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value(ErrorCode.INVALID_PERMISSION.name()))
                    .andExpect(jsonPath("$.result.message").value(ErrorCode.INVALID_PERMISSION.getMessage()))
                    .andDo(print());

            verify(commentService).uploadComment(any(), any(), eq(1));
        }
    }

    @Nested
//    @Order(1)
    @DisplayName("댓글 조회")
    class CommentAcquisition {

        @Test
        @DisplayName("성공")
        @WithMockUser
        void success_list_comments() throws Exception {
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            List<CommentResponse> comments = List.of(commentResponse);

            Page<CommentResponse> pagedComments = new PageImpl<>(comments);

            given(commentService.fetchComments(any(), eq(postId))).willReturn(pagedComments);

            mockMvc.perform(get(url + "/comments")
                            .content(objectMapper.writeValueAsBytes(pageable)).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.content").exists())
                    .andDo(print());

            verify(commentService).fetchComments(any(), eq(postId));
        }
    }

    @Nested
    @DisplayName("댓글 수정")
    class CommentEdition {
        @Test
        @DisplayName("성공")
        @WithMockUser
        void success_edit_a_comment() throws Exception {
            given(commentService.modifyComment(any(), eq(commentId), any())).willReturn(commentResponse);

            mockMvc.perform(put(url + "/comments/" + commentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(commentRequest)).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.id").value(commentId))
                    .andExpect(jsonPath("$.result.comment").value(commentResponse.getComment()))
                    .andExpect(jsonPath("$.result.userName").value(commentResponse.getUserName()))
                    .andExpect(jsonPath("$.result.postId").value(commentResponse.getPostId()))
                    .andExpect(jsonPath("$.result.createdAt").value(commentResponse.getCreatedAt().toString()))
                    .andDo(print());

            verify(commentService).modifyComment(any(), eq(commentId), any());
        }

        @ParameterizedTest
        @DisplayName("실패")
        @WithMockUser
        @MethodSource("com.example.likelionfinalproject.controller.PostControllerTest#provideErrorCase")
        void fail_edit_a_comment(ResultMatcher error, ErrorCode code) throws Exception {
            given(commentService.modifyComment(any(), eq(commentId), any()))
                    .willThrow(new UserException(code, code.getMessage()));

            mockMvc.perform(put(url + "/comments/" + commentId)
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(commentRequest))
                    .with(csrf()))
                    .andExpect(error)
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value(code.name()))
                    .andExpect(jsonPath("$.result.message").value(code.getMessage()))
                    .andDo(print());

            verify(commentService).modifyComment(any(), eq(commentId), any());

        }
    }

    private void confirmSuccess(MockHttpServletRequestBuilder httpType, ResponseDto responseDto) throws Exception {
        mockMvc.perform(httpType.with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").value(responseDto.getMessage()))
                .andExpect(jsonPath("$.result.id").value(responseDto.getId()))
                .andDo(print());
    }

    @Nested
    @DisplayName("댓글 삭제")
    class CommentRemoval {

        @Test
        @DisplayName("성공")
        @WithMockUser
        void success_delete_comment() throws Exception {
            CommentDeleteResponse deleteResponse = CommentDeleteResponse.builder().id(commentId).build();
            deleteResponse.setMessage("댓글 삭제 완료");
            
            given(commentService.removeComment(eq(commentId), any())).willReturn(deleteResponse);

            confirmSuccess(delete(url + "/comments/" + commentId), deleteResponse);

            verify(commentService).removeComment(eq(commentId), any());
        }

        @ParameterizedTest
        @DisplayName("실패")
        @WithMockUser
        @MethodSource("com.example.likelionfinalproject.controller.PostControllerTest#provideErrorCase")
        void fail_delete_a_comment(ResultMatcher error, ErrorCode code) throws Exception {
            given(commentService.removeComment(eq(commentId), any()))
                    .willThrow(new UserException(code, code.getMessage()));

            mockMvc.perform(delete(url + "/comments/" + commentId)
                            .with(csrf()))
                    .andExpect(error)
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value(code.name()))
                    .andExpect(jsonPath("$.result.message").value(code.getMessage()))
                    .andDo(print());

            verify(commentService).removeComment(eq(commentId), any());

        }
    }

    @Nested
    @DisplayName("마이 피드 조회")
    class PersonalPosts {

        @Test
        @DisplayName("성공")
        @WithMockUser
        void success_show_my_posts() throws Exception {
            given(postService.showMyPosts(any(), any())).willReturn(posts);

            mockMvc.perform(get("/api/v1/posts/my").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.content").exists())
                    .andDo(print());

            verify(postService).showMyPosts(any(), any());
        }
        
        @Test
        @DisplayName("실패")
//        @WithAnonymousUser
        void fail_show_my_posts() throws Exception {
            mockMvc.perform(get("/api/v1/posts/my")
                            .header(HttpHeaders.AUTHORIZATION, "").with(csrf()))
                    .andExpect(status().isUnauthorized())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("좋아요 등록")
    class LikeGeneration {

        /* 좋아요가 눌릴 시 서비스단 likeService 에서 성공 메시지를 반환하는 상황을 가정한다. */
        @Test
        @DisplayName("성공")
        @WithMockUser
        void success_generate_like() throws Exception {
            String response = "좋아요를 눌렀습니다.";

            /* 서비스에서는 포스트 아이디와 사용자 아이디를 매개 변수로 받는다. */
            given(likeService.pushThumbsUp(any(), any())).willReturn(response);

            mockMvc.perform(post(url + "/likes")
                    .contentType(MediaType.APPLICATION_JSON).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").value(response))
                    .andDo(print());

            verify(likeService).pushThumbsUp(any(), any());
        }

        @Test
        @DisplayName("실패 - 로그인 하지 않음")
        @WithAnonymousUser
        void fail_generate_like_not_a_user() throws Exception {
            mockMvc.perform(post(url + "/likes")
                    .contentType(MediaType.APPLICATION_JSON).with(csrf()))
                    .andExpect(status().isUnauthorized())
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 포스트 없음")
        @WithMockUser
        void fail_generate_like_no_post() throws Exception {
            given(likeService.pushThumbsUp(eq(1), any()))
                    .willThrow(new UserException(ErrorCode.POST_NOT_FOUND, ErrorCode.POST_NOT_FOUND.getMessage()));

            mockMvc.perform(post(url + "/likes")
                            .contentType(MediaType.APPLICATION_JSON).with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value(ErrorCode.POST_NOT_FOUND.name()))
                    .andExpect(jsonPath("$.result.message").value(ErrorCode.POST_NOT_FOUND.getMessage()))
                    .andDo(print());

            verify(likeService).pushThumbsUp(eq(1), any());
        }
    }
}
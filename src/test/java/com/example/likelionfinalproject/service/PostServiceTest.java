package com.example.likelionfinalproject.service;

import com.example.likelionfinalproject.domain.dto.EditPostRequest;
import com.example.likelionfinalproject.domain.dto.PostRequest;
import com.example.likelionfinalproject.domain.dto.PostResponse;
import com.example.likelionfinalproject.domain.dto.SelectedPostResponse;
import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.User;
import com.example.likelionfinalproject.exception.ErrorCode;
import com.example.likelionfinalproject.exception.SocialAppException;
import com.example.likelionfinalproject.exception.UserException;
import com.example.likelionfinalproject.fixture.PostFixture;
import com.example.likelionfinalproject.fixture.UserFixture;
import com.example.likelionfinalproject.repository.PostRepository;
import com.example.likelionfinalproject.repository.UserRepository;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestExecutionListeners;

import javax.swing.text.html.Option;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


class PostServiceTest {

    PostRepository postRepository = Mockito.mock(PostRepository.class);

    UserRepository userRepository = Mockito.mock(UserRepository.class);
    PostService postService;
    User mockUser;
    Post mockPost;
    Integer postId;
    String mockAuthorId;
    EditPostRequest editPostRequest;
    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository, userRepository);

        postId = 1;

        mockUser = UserFixture.get();
        mockAuthorId = "작성자";

        mockPost = PostFixture.get(postId);

        editPostRequest = EditPostRequest.builder()
                .body("body")
                .title("title")
                .build();

    }

    @Test
    @DisplayName("포스트 등록 성공")
    void success_add_post() {
        when(userRepository.findByUserName(mockUser.getUserName())).thenReturn(Optional.of(mockPost.getAuthor()));
        when(postRepository.save(any())).thenReturn(mockPost);

        PostResponse postResponse = postService.createPost(mockPost.toRequest(), mockUser.getUserName());

        Assertions.assertDoesNotThrow(()->new UserException(ErrorCode.USERNAME_NOT_FOUND,
                mockUser.getUserName() + "은 없는 아이디입니다."));

        Assertions.assertEquals(mockPost.getId(), postResponse.getPostId());

        verify(postRepository).save(any());
        verify(userRepository).findByUserName(mockUser.getUserName());
    }

    @Test
    @DisplayName("포스트 등록 실패")
    void fail_add_post() {
        when(userRepository.findByUserName(mockUser.getUserName())).thenReturn(Optional.empty());

        UserException e = Assertions.assertThrows(UserException.class,
                ()->postService.createPost(mockPost.toRequest(), mockUser.getUserName()));

        Assertions.assertEquals(ErrorCode.USERNAME_NOT_FOUND, e.getErrorCode());

        verify(userRepository).findByUserName(mockUser.getUserName());
    }

    @Test
    @DisplayName("포스트 조회 성공")
    void success_fetch_post_info() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));

        SelectedPostResponse response = postService.acquirePost(postId);

        Assertions.assertEquals(mockPost.getAuthor().getUserName(), response.getUserName());

        verify(postRepository).findById(postId);
    }

    @Test
    @DisplayName("수정하려는 포스트가 존재하지 않아 수정에 실패한다.")
    void fail_edit_post_not_found() {
        when(postRepository.findById(mockPost.getId())).thenReturn(Optional.empty());

        UserException e = Assertions.assertThrows(UserException.class,
                ()->postService.editPost(editPostRequest, mockPost.getId(), "작성자1"));

        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());

    }

    @Test
    @DisplayName("수정하려는 포스트의 작성자와 로그인된 사용자가 달라 수정에 실패한다.")
    void fail_edit_post_not_allowed_user() {
        User author = User.builder()
                .userName("작성자1")
                .build();

        User currentUser = User.builder().userName("작성자2")
                .build();

        Post postWithAuthor = Post.builder()
                .author(author)
                .title("제목")
                .body("내용")
                .build();

        when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(postWithAuthor));

        when(userRepository.findByUserName(postWithAuthor.getAuthor().getUserName())).thenReturn(Optional.of(author));

        UserException e = Assertions.assertThrows(UserException.class,
                ()->postService.editPost(any(), mockPost.getId(), currentUser.getUserName()));

        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    @Test
    @DisplayName("현재 DB에 더 이상 포스트를 작성했던 사용자가 없어서 수정에 실패한다.")
    void fail_edit_post_user_absent() {
        when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(mockPost));

        when(userRepository.findByUserName(mockPost.getAuthor().getUserName())).thenReturn(Optional.empty());

        UserException e = Assertions.assertThrows(UserException.class,
                ()->postService.editPost(editPostRequest, mockPost.getId(), any()));

        Assertions.assertEquals(ErrorCode.USERNAME_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("사용자가 존재하지 않아 포스트 삭제에 실패한다.")
    void fail_delete_post_user_absent() {
        when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(mockPost));

        when(userRepository.findByUserName(mockPost.getAuthor().getUserName())).thenReturn(Optional.empty());

        UserException e = Assertions.assertThrows(UserException.class,
                ()->postService.removePost(mockPost.getId(), mockPost.getAuthor().getUserName()));

        Assertions.assertEquals(ErrorCode.USERNAME_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("포스트가 존재하지 않아 포스트 삭제에 실패한다.")
    void fail_delete_post_absent() {
        when(postRepository.findById(mockPost.getId())).thenReturn(Optional.empty());

        UserException e = Assertions.assertThrows(UserException.class,
                ()->postService.removePost(mockPost.getId(), mockPost.getAuthor().getUserName()));

        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("작성자와 유저가 일치하지 않아 포스트 삭제에 실패한다.")
    void fail_delete_inconsistent_author() {
        User author = User.builder()
                .userName("작성자1")
                .build();

        User currentUser = User.builder().userName("작성자2")
                .build();

        Post postWithAuthor = Post.builder()
                .author(author)
                .title("제목")
                .body("내용")
                .build();

        when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(postWithAuthor));

        when(userRepository.findByUserName(postWithAuthor.getAuthor().getUserName())).thenReturn(Optional.of(author));

        UserException e = Assertions.assertThrows(UserException.class,
                ()->postService.removePost(mockPost.getId(), currentUser.getUserName()));

        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    
}
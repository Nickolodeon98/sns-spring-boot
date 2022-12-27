package com.example.likelionfinalproject.service;

import com.example.likelionfinalproject.domain.dto.PostRequest;
import com.example.likelionfinalproject.domain.dto.PostResponse;
import com.example.likelionfinalproject.domain.dto.SelectedPostResponse;
import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.User;
import com.example.likelionfinalproject.exception.ErrorCode;
import com.example.likelionfinalproject.exception.UserException;
import com.example.likelionfinalproject.repository.PostRepository;
import com.example.likelionfinalproject.repository.UserRepository;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.test.context.support.WithMockUser;

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
    User postAuthor;
    Post mockPost;
    Integer postId;
    String mockAuthorId;
    PostRequest postRequest;
    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository, userRepository);

        postId = 1;

        mockAuthorId = "작성자";

        postAuthor = User.builder()
                .userName("작성자")
                .build();

        mockPost = Post.builder()
                .id(postId)
                .author(postAuthor)
                .title("제목")
                .body("내용")
                .build();

         postRequest = PostRequest.builder()
                .title("제목")
                .body("내용")
                .build();
    }

    @Test
    @DisplayName("주어진 정보대로 포스트 등록에 성공한다.")
    void success_add_post() {
        when(postRepository.save(any())).thenReturn(mockPost);
        when(userRepository.findByUserName(mockAuthorId)).thenReturn(Optional.of(postAuthor));

        PostResponse postResponse = postService.createNewPost(postRequest, mockAuthorId);

        Assertions.assertDoesNotThrow(()->new UserException(ErrorCode.USERNAME_NOT_FOUND, mockAuthorId + "은 없는 아이디입니다."));
        Assertions.assertEquals(mockPost.getId(), postResponse.getPostId());

        verify(postRepository).save(any());
        verify(userRepository).findByUserName(mockAuthorId);
    }

    @Test
    @DisplayName("로그인 하지 않아 포스트 등록에 실패한다.")
    void fail_add_post() {
        when(userRepository.findByUserName(mockAuthorId)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserException.class, ()->postService.createNewPost(postRequest, mockAuthorId));
        verify(userRepository).findByUserName(mockAuthorId);
    }

    @Test
    @DisplayName("조회하려는 포스트를 찾아 반환한다.")
    void fetch_post_info() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));

        SelectedPostResponse response = postService.acquireSinglePost(postId);

        Assertions.assertEquals(mockPost.getAuthor().getUserName(), response.getUserName());

        verify(postRepository).findById(postId);
    }

}
package com.example.likelionfinalproject.service;

import com.example.likelionfinalproject.domain.dto.SelectedPostResponse;
import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.User;
import com.example.likelionfinalproject.repository.PostRepository;
import com.example.likelionfinalproject.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class PostServiceTest {

    PostRepository postRepository = Mockito.mock(PostRepository.class);

    UserRepository userRepository = Mockito.mock(UserRepository.class);
    PostService postService;

    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository, userRepository);
    }

    @Test
    @DisplayName("조회하고자 하는 포스트를 찾아 반환한다.")
    void fetch_post_info() {

        Long postsId = 1L;

        User postAuthor = User.builder()
                .userName("작성자")
                .build();

        Post foundPost = Post.builder()
                .id(postsId)
                .author(postAuthor)
                .title("제목")
                .body("내용")
                .build();

        when(postRepository.findById(postsId)).thenReturn(Optional.of(foundPost));

        SelectedPostResponse response = postService.acquireSinglePost(postsId);

        Assertions.assertEquals(foundPost.getAuthor().getUserName(), response.getUserName());

        verify(postRepository).findById(postsId);
    }

}
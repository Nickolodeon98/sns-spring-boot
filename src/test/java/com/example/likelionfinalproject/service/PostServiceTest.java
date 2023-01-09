package com.example.likelionfinalproject.service;

import com.example.likelionfinalproject.domain.dto.PostResponse;
import com.example.likelionfinalproject.domain.dto.SelectedPostResponse;
import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.UserEntity;
import com.example.likelionfinalproject.exception.ErrorCode;
import com.example.likelionfinalproject.exception.UserException;
import com.example.likelionfinalproject.fixture.PostFixture;
import com.example.likelionfinalproject.fixture.UserFixture;
import com.example.likelionfinalproject.repository.PostRepository;
import com.example.likelionfinalproject.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    PostRepository postRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    PostService postService;
    UserEntity mockUserEntity;
    Post mockPost;
    Integer postId;
    @BeforeEach
    void setUp() {
        postId = 1;
        mockUserEntity = UserFixture.get();
        mockPost = PostFixture.get(postId);
    }
    /* 테스트 코드마다 같은 패턴에 다른 인수들이 사용되고 있으므로 다른 인수를 주입하는 메서드를 정의한다. */
    private static Stream<Arguments> provideObjectAndErrorCase() {
        Post mockPost = PostFixture.get();
        UserEntity mockCurrentUserEntity = UserFixture.get("다른 작성자");
        return Stream.of(Arguments.of(Named.of("포스트 없음", ErrorCode.POST_NOT_FOUND),
                        Optional.empty(),
                        Optional.of(mockPost.getAuthor()),
                        mockPost.getAuthor().getUserName()),
                Arguments.of( Named.of("작성자 없음", ErrorCode.USERNAME_NOT_FOUND),
                        Optional.of(mockPost),
                        Optional.empty(),
                        mockPost.getAuthor().getUserName()),
                Arguments.of( Named.of("작성자 사용자 불일치", ErrorCode.INVALID_PERMISSION),
                        Optional.of(mockPost),
                        Optional.of(mockPost.getAuthor()),
                        mockCurrentUserEntity.getUserName()));
    }
    @Nested
    @DisplayName("포스트 등록")
    class PostCreation {
        @Test
        @DisplayName("성공")
        void success_add_post() {
            when(userRepository.findByUserName(mockUserEntity.getUserName())).thenReturn(Optional.of(mockPost.getAuthor()));
            when(postRepository.save(any())).thenReturn(mockPost);

            PostResponse postResponse = postService.createPost(mockPost.toRequest(), mockUserEntity.getUserName());

            Assertions.assertDoesNotThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND,
                    mockUserEntity.getUserName() + "은 없는 아이디입니다."));

            Assertions.assertEquals(mockPost.getId(), postResponse.getPostId());

            verify(postRepository).save(any());
            verify(userRepository).findByUserName(mockUserEntity.getUserName());
        }

        @Test
        @DisplayName("실패")
        void fail_add_post() {
            when(userRepository.findByUserName(mockUserEntity.getUserName())).thenReturn(Optional.empty());

            UserException e = Assertions.assertThrows(UserException.class,
                    () -> postService.createPost(mockPost.toRequest(), mockUserEntity.getUserName()));

            Assertions.assertEquals(ErrorCode.USERNAME_NOT_FOUND, e.getErrorCode());

            verify(userRepository).findByUserName(mockUserEntity.getUserName());
        }
    }
    @Nested
    @DisplayName("포스트 조회")
    class PostAcquisition {
        @Test
        @DisplayName("성공")
        void success_fetch_post_info() {
            when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(mockPost));

            SelectedPostResponse response = postService.acquirePost(mockPost.getId());

            Assertions.assertEquals(mockPost.getAuthor().getUserName(), response.getUserName());

            verify(postRepository).findById(mockPost.getId());
        }
    }
    @Nested
    @DisplayName("포스트 수정")
    class PostEdition {
        @ParameterizedTest
        @DisplayName("실패")
        @MethodSource("com.example.likelionfinalproject.service.PostServiceTest#provideObjectAndErrorCase")
        void fail_edit_post(ErrorCode code, Optional optionalPost, Optional optionalUser, String userName) {
            Mockito.lenient().when(postRepository.findById(mockPost.getId())).thenReturn(optionalPost);

            Mockito.lenient().when(userRepository.findByUserName(mockPost.getAuthor().getUserName())).thenReturn(optionalUser);

            UserException e = Assertions.assertThrows(UserException.class,
                    () -> postService.editPost(mockPost.toRequest(), mockPost.getId(), userName));

            Assertions.assertEquals(code, e.getErrorCode());
        }
    }
    @Nested
    @DisplayName("포스트 삭제")
    class PostRemoval {
        @ParameterizedTest
        @DisplayName("실패")
        @MethodSource("com.example.likelionfinalproject.service.PostServiceTest#provideObjectAndErrorCase")
        void fail_delete_post(ErrorCode code, Optional optionalPost, Optional optionalUser, String userName) {
            Mockito.lenient().when(postRepository.findById(mockPost.getId())).thenReturn(optionalPost);

            Mockito.lenient().when(userRepository.findByUserName(mockPost.getAuthor().getUserName())).thenReturn(optionalUser);

            UserException e = Assertions.assertThrows(UserException.class,
                    () -> postService.removePost(mockPost.getId(), userName));

            Assertions.assertEquals(code, e.getErrorCode());
        }
    }
}
package com.example.likelionfinalproject.service;

import com.example.likelionfinalproject.domain.dto.request.CommentRequest;
import com.example.likelionfinalproject.domain.dto.response.CommentResponse;
import com.example.likelionfinalproject.domain.entity.Comment;
import com.example.likelionfinalproject.domain.entity.UserEntity;
import com.example.likelionfinalproject.exception.ErrorCode;
import com.example.likelionfinalproject.exception.UserException;
import com.example.likelionfinalproject.fixture.CommentFixture;
import com.example.likelionfinalproject.fixture.UserFixture;
import com.example.likelionfinalproject.repository.AlarmRepository;
import com.example.likelionfinalproject.repository.CommentRepository;
import com.example.likelionfinalproject.repository.PostRepository;
import com.example.likelionfinalproject.repository.UserRepository;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CommentServiceTest {

    final CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    final UserRepository userRepository = Mockito.mock(UserRepository.class);
    final PostRepository postRepository = Mockito.mock(PostRepository.class);
    final AlarmRepository alarmRepository = Mockito.mock(AlarmRepository.class);

    final CommentService commentService  = new CommentService(commentRepository, userRepository, postRepository, alarmRepository);
    CommentRequest commentRequest;
    Comment commentA;
    Comment commentB;

    UserEntity userA;
    UserEntity userB;

    @BeforeEach
    void setUp() {
        userA = UserFixture.get(1, "user A");
        userB = UserFixture.get(2, "user B");
        commentA = CommentFixture.get(userA, "댓글 내용 1");
        commentB = CommentFixture.get(userA, "댓글 내용 2");
        commentRequest = CommentRequest.of(commentB);
    }

    public static Stream<Arguments> provideErrorCases() {
        UserEntity userA = UserFixture.get(1, "user A");
        UserEntity userB = UserFixture.get(2, "user B");
        Comment commentA = CommentFixture.get(userA, "댓글 내용 1");

        return Stream.of(Arguments.of(Named.of("사용자 존재하지 않음", ErrorCode.USERNAME_NOT_FOUND), Optional.of(commentA), Optional.empty(), userA.getUserName()),
                Arguments.of(Named.of("댓글 존재하지 않음", ErrorCode.COMMENT_NOT_FOUND), Optional.empty(), Optional.of(userA), userA.getUserName()),
                Arguments.of(Named.of("작성자와 사용자가 불일치함", ErrorCode.INVALID_PERMISSION), Optional.of(commentA), Optional.of(userA), userB.getUserName()));
    }

    @Nested
    @DisplayName("댓글 수정")
    class CommentEdition {

        @ParameterizedTest
        @DisplayName("실패")
        @MethodSource("com.example.likelionfinalproject.service.CommentServiceTest#provideErrorCases")
        void fail_edit_comment(ErrorCode code, Optional<Comment> comment, Optional<UserEntity> user, String userName) {
            when(commentRepository.findById(commentA.getId())).thenReturn(comment);
            when(userRepository.findByUserName(commentA.getAuthor().getUserName())).thenReturn(user);

            UserException e = Assertions.assertThrows(UserException.class,
                    ()->commentService.modifyComment(commentRequest, commentB.getId(), userName));

            Assertions.assertEquals(code, e.getErrorCode());
        }

    }

    @Nested
    @DisplayName("댓글 삭제")
    class CommentDeletion {

        @DisplayName("실패")
        @ParameterizedTest
        @MethodSource("com.example.likelionfinalproject.service.CommentServiceTest#provideErrorCases")
        void fail_delete_comment(ErrorCode code, Optional<Comment> comment, Optional<UserEntity> user, String userName) {
            when(commentRepository.findById(commentA.getId())).thenReturn(comment);
            when(userRepository.findByUserName(commentA.getAuthor().getUserName())).thenReturn(user);

            UserException e = Assertions.assertThrows(UserException.class,
                    () -> commentService.removeComment(commentA.getId(), userName));

            Assertions.assertEquals(code, e.getErrorCode());

        }
    }
}
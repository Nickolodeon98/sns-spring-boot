package com.example.likelionfinalproject.service;

import com.example.likelionfinalproject.domain.dto.*;
import com.example.likelionfinalproject.domain.entity.Comment;
import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.UserEntity;
import com.example.likelionfinalproject.exception.ErrorCode;
import com.example.likelionfinalproject.exception.UserException;
import com.example.likelionfinalproject.repository.AlarmRepository;
import com.example.likelionfinalproject.repository.CommentRepository;
import com.example.likelionfinalproject.repository.PostRepository;
import com.example.likelionfinalproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final PostRepository postRepository;
    private final AlarmRepository alarmRepository;

    public CommentResponse uploadComment(CommentRequest commentRequest, String userName, Integer postId) {
        /* TODO: commentRequest 로 부터 comment 내용을 받아서 Comment 엔티티에 저장한다. */
        UserEntity userEntity = userRepository.findByUserName(userName)
                .orElseThrow(()->new UserException(ErrorCode.USERNAME_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(()->new UserException(ErrorCode.POST_NOT_FOUND));

        if (post.getDeletedAt() != null)
            throw new UserException(ErrorCode.POST_NOT_FOUND);

        Comment savedComment = commentRepository.save(commentRequest.toEntity(post, userEntity));

        AlarmRequest alarmRequest = AlarmRequest.builder()
                .alarmType(AlarmType.NEW_COMMENT_ON_POST)
                .fromUserId(userEntity.getId())
                .targetId(post.getAuthor().getId())
                .text("new comment!")
                .build();

        alarmRepository.save(alarmRequest.toEntity(userEntity));

        return CommentResponse.of(savedComment);
    }

    public Page<CommentResponse> fetchComments(Pageable pageable, Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new UserException(ErrorCode.POST_NOT_FOUND));

        if (post.getDeletedAt() != null)
            throw new UserException(ErrorCode.POST_NOT_FOUND);
//        Page<Comment> comments = new PageImpl<>(post.getComments());

        Page<Comment> comments = commentRepository.findAllByPostId(postId, pageable);

        return comments.map(CommentResponse::of);
    }

    private Comment validate(Integer commentId, String userName) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new UserException(ErrorCode.COMMENT_NOT_FOUND));

        if (comment.getDeletedAt() != null)
            throw new UserException(ErrorCode.COMMENT_NOT_FOUND);

        if (!userName.equals(comment.getAuthor().getUserName()))
            throw new UserException(ErrorCode.INVALID_PERMISSION);

        /* TODO: 데이터베이스 에러가 발생하는 상황을 생각해보고 예외 처리한다. */

        return comment;
    }

    public CommentResponse modifyComment(CommentRequest commentRequest, Integer commentId, String userName) {
        Comment comment = validate(commentId, userName);

        /* Comment 객체를 변경하는 것이 맞을지, 댓글 요청 DTO 를 사용해 새로운 Comment 객체를 생성하는 것이 맞을 지 고민이다.
         * 새로운 객체를 생성하는 것은 공간 복잡도를 늘리기 때문에 setter 를 사용해 기존 Comment 객체를 업데이트한다.
         * 뿐만 아니라 새로운 객체를 생성하더라도 기존 댓글의 아이디를 포함한 정보를 넣어주면 created_at 은 null 이 된다.
         * 이는 updatable = false 이기 때문이다. */
        comment.setComment(commentRequest.getComment());

        Comment savedComment = commentRepository.save(comment);

        return CommentResponse.of(savedComment);
    }

    public CommentDeleteResponse removeComment(Integer commentId, String userName) {
        Comment comment = validate(commentId, userName);

        commentRepository.delete(comment);

        return CommentDeleteResponse.of(comment);
    }
}

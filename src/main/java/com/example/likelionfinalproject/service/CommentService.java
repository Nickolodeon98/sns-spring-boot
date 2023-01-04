package com.example.likelionfinalproject.service;

import com.example.likelionfinalproject.domain.dto.CommentDeleteResponse;
import com.example.likelionfinalproject.domain.dto.CommentRequest;
import com.example.likelionfinalproject.domain.dto.CommentResponse;
import com.example.likelionfinalproject.domain.entity.Comment;
import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.User;
import com.example.likelionfinalproject.exception.ErrorCode;
import com.example.likelionfinalproject.exception.UserException;
import com.example.likelionfinalproject.repository.CommentRepository;
import com.example.likelionfinalproject.repository.PostRepository;
import com.example.likelionfinalproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    public CommentResponse uploadComment(CommentRequest commentRequest, String userName, Integer postId) {
        /* TODO: commentRequest 로 부터 comment 내용을 받아서 Comment 엔티티에 저장한다. */
        User user = userRepository.findByUserName(userName)
                .orElseThrow(()->new UserException(ErrorCode.USERNAME_NOT_FOUND, ErrorCode.USERNAME_NOT_FOUND.getMessage()));

        Post post = postRepository.findById(postId)
                .orElseThrow(()->new UserException(ErrorCode.POST_NOT_FOUND, ErrorCode.POST_NOT_FOUND.getMessage()));

        Comment savedComment = commentRepository.save(commentRequest.toEntity(post, user));

        return CommentResponse.of(savedComment);
    }

    public Page<CommentResponse> fetchComments(Pageable pageable, Integer postId) {
        postRepository.findById(postId)
                .orElseThrow(()->new UserException(ErrorCode.POST_NOT_FOUND, ErrorCode.POST_NOT_FOUND.getMessage()));

//        Page<Comment> comments = new PageImpl<>(post.getComments());

        Page<Comment> comments = commentRepository.findAllByPostId(postId, pageable);

        return comments.map(CommentResponse::of);
    }

    private Comment validate(Integer commentId, String userName) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new UserException(ErrorCode.COMMENT_NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND.getMessage()));

        if (!userName.equals(comment.getAuthor().getUserName()))
            throw new UserException(ErrorCode.INVALID_PERMISSION, ErrorCode.INVALID_PERMISSION.getMessage());

        /* TODO: 데이터베이스 에러가 발생하는 상황을 생각해보고 예외 처리한다. */

        return comment;
    }

    public CommentResponse modifyComment(CommentRequest commentRequest, Integer commentId, String userName) {
        Comment comment = validate(commentId, userName);

        /* Comment 객체를 변경하는 것이 맞을지, 댓글 요청 DTO 를 사용해 새로운 Comment 객체를 생성하는 것이 맞을 지 고민이다.
         * 새로운 객체를 생성하는 것은 공간 복잡도를 늘리기 때문에 setter 를 사용해 기존 Comment 객체를 업데이트한다. */
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

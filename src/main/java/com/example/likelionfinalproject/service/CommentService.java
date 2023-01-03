package com.example.likelionfinalproject.service;

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
}

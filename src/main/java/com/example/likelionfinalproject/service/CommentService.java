package com.example.likelionfinalproject.service;

import com.example.likelionfinalproject.domain.dto.CommentRequest;
import com.example.likelionfinalproject.domain.dto.CommentResponse;
import com.example.likelionfinalproject.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentResponse uploadComment(CommentRequest commentRequest, String userName) {
        /* TODO: commentRequest 로 부터 comment 내용을 받아서 Comment 엔티티에 저장한다. */

        Comment savedComment = commentRepository.save(commentRequest.toEntity());

        return CommentResponse.of(savedComment);
    }
}

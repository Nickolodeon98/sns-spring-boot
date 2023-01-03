package com.example.likelionfinalproject.service;

import com.example.likelionfinalproject.domain.dto.CommentRequest;
import com.example.likelionfinalproject.domain.dto.CommentResponse;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    public CommentResponse uploadComment(CommentRequest commentRequest, String userName) {
        /* TODO: commentRequest 로 부터 comment 내용을 받아서 Comment 엔티티에 저장한다. */
    }
}

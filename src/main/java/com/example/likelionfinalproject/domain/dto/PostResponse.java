package com.example.likelionfinalproject.domain.dto;

import com.example.likelionfinalproject.domain.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@AllArgsConstructor
@Builder
@Slf4j
public class PostResponse {

    private String message;
    private Integer postId;

    public static PostResponse of(Post savedPost) {

        return PostResponse.builder()
                .postId(savedPost.getId())
                .message("포스트 등록 완료")
                .build();
    }

    public static PostResponse build(String message, Integer postId) {
        return PostResponse.builder()
                .postId(postId)
                .message(message)
                .build();
    }
}

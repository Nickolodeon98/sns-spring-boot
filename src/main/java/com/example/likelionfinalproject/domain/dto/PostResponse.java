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

    public static PostResponse of(Post post, String message) {

        return PostResponse.builder()
                .postId(post.getId())
                .message(message)
                .build();
    }
}

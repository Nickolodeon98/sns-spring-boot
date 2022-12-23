package com.example.likelionfinalproject.domain.dto;

import com.example.likelionfinalproject.domain.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PostResponse {

    private String message;
    private Long postId;

    public static PostResponse of(Post savedPost) {

        return PostResponse.builder()
                .postId(savedPost.getId())
                .message("포스트 등록 완료")
                .build();
    }
}

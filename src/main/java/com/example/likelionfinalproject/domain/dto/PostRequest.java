package com.example.likelionfinalproject.domain.dto;

import com.example.likelionfinalproject.domain.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PostRequest {

    private String title;
    private String body;

    public Post toEntity() {
        return Post.builder()
                .title(title)
                .body(body)
                .build();
    }
}

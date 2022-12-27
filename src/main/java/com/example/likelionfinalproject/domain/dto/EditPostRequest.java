package com.example.likelionfinalproject.domain.dto;

import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class EditPostRequest {

    private String title;
    private String body;

    public Post toEntity(Integer postId, User author) {
        return Post.builder()
                .id(postId)
                .title(title)
                .body(body)
                .author(author)
                .build();
    }
}

package com.example.likelionfinalproject.domain.dto.request;

import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Data
public class PostRequest {

    private String title;
    private String body;

    public Post toEntity() {
        return Post.builder()
                .title(title)
                .body(body)
                .build();
    }

    public Post toEntity(Integer postId, UserEntity author) {
        return Post.builder()
                .id(postId)
                .title(title)
                .body(body)
                .author(author)
                .build();
    }
}

package com.example.likelionfinalproject.domain.dto;

import com.example.likelionfinalproject.domain.entity.Post;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Setter
@Slf4j
public class PostResponse extends ResponseDto {

    private Integer postId;

    public static PostResponse of(Post post, String message) {
        PostResponse postResponse = PostResponse.builder().postId(post.getId()).build();
        postResponse.setMessage(message);
        return postResponse;
    }
}

package com.example.likelionfinalproject.fixture;

import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.User;

public class PostFixture {

    public static Post get(Integer postId) {
        User user = UserFixture.get();
        return Post.builder()
                .id(postId)
                .author(user)
                .title("제목")
                .body("내용")
                .build();
    }
}

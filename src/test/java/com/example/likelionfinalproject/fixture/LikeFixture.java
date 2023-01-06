package com.example.likelionfinalproject.fixture;


import com.example.likelionfinalproject.domain.entity.Like;
import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.User;

public class LikeFixture {

    public static Like get(Post post, User user) {
        return Like.builder()
                .post(post)
                .user(user)
                .build();
    }

}

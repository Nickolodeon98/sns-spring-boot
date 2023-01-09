package com.example.likelionfinalproject.fixture;


import com.example.likelionfinalproject.domain.entity.LikeEntity;
import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.UserEntity;

public class LikeFixture {

    public static LikeEntity get(Post post) {
        return LikeEntity.builder()
                .post(post)
                .build();
    }

    public static LikeEntity get(Post post, UserEntity userEntity) {
        return LikeEntity.builder()
                .post(post)
                .userEntity(userEntity)
                .build();
    }

}

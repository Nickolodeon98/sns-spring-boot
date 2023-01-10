package com.example.likelionfinalproject.fixture;

import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.UserEntity;

public class PostFixture {

    public static Post get() {
        UserEntity userEntity = UserFixture.get();
        return Post.builder()
                .id(1)
                .author(userEntity)
                .title("제목")
                .body("내용")
                .build();
    }
    
    // 매개 변수가 있는 Post 엔티티 생성 메서드
    public static Post get(Integer postId) {
        UserEntity userEntity = UserFixture.get();
        return Post.builder()
                .id(postId)
                .author(userEntity)
                .title("제목")
                .body("내용")
                .build();
    }

    public static Post get(Integer postId, UserEntity userEntity) {
        return Post.builder()
                .id(postId)
                .author(userEntity)
                .title("제목")
                .body("내용")
                .build();
    }
}

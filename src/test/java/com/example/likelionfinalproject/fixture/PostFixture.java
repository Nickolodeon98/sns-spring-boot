package com.example.likelionfinalproject.fixture;

import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.User;

public class PostFixture {

    public static Post get() {
        User user = UserFixture.get();
        return Post.builder()
                .id(1)
                .author(user)
                .title("제목")
                .body("내용")
                .build();
    }
    
    // 매개 변수가 있는 Post 엔티티 생성 메서드
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
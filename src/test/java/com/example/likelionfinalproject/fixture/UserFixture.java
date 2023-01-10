package com.example.likelionfinalproject.fixture;

import com.example.likelionfinalproject.domain.entity.UserEntity;

public class UserFixture {

    public static UserEntity get() {
        return UserEntity.builder()
                .userName("작성자")
                .build();
    }

    // 매개 변수가 있는 User 엔티티 생성 메서드
    public static UserEntity get(String name) {
        return UserEntity.builder()
                .userName(name)
                .build();
    }

    public static UserEntity get(Integer userId, String name) {
        return UserEntity.builder()
                .id(userId)
                .userName(name)
                .build();
    }
}

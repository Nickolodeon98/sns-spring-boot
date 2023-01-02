package com.example.likelionfinalproject.fixture;

import com.example.likelionfinalproject.domain.entity.User;

public class UserFixture {

    public static User get() {
        return User.builder()
                .userName("작성자")
                .build();
    }

    // 매개 변수가 있는 User 엔티티 생성 메서드
    public static User get(String name) {
        return User.builder()
                .userName(name)
                .build();
    }
}

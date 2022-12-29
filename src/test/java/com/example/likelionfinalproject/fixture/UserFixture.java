package com.example.likelionfinalproject.fixture;

import com.example.likelionfinalproject.domain.entity.User;

public class UserFixture {

    public static User get() {
        return User.builder()
                .userName("작성자")
                .build();
    }
}

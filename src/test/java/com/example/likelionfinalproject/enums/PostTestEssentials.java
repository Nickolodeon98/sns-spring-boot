package com.example.likelionfinalproject.enums;

import lombok.Getter;

@Getter
public enum PostTestEssentials {
    USER_NAME("sjeon0730"),
    PASSWORD("1q2w3e4r"),
    POST_URL("/api/v1/posts/"),
    TOKEN("123456789");

    private final String value;
    PostTestEssentials(String value) {
        this.value = value;
    }
}

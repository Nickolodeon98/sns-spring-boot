package com.example.likelionfinalproject.enums;

import lombok.Getter;

@Getter
public enum UserTestEssentials {
    USER_NAME("sjeon0730"),
    PASSWORD("1q2w3e4r"),
    USER_URL("/api/v1/users/"),
    TOKEN("123456789");

    private final String value;
    UserTestEssentials(String value) {
        this.value = value;
    }
}

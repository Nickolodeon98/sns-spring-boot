package com.example.likelionfinalproject.enums;

import lombok.Getter;

@Getter
public enum PostTestEssentials {
    POST_URL("/api/v1/posts/"),
    POST_TITLE("포스트 제목"),
    POST_BODY("포스트 내용"),
    POST_CREATE_MESSAGE("포스트 등록 완료"),
    POST_EDIT_MESSAGE("포스트 수정 완료"),
    POST_DELETE_MESSAGE("포스트 삭제 완료");

    private final String value;
    PostTestEssentials(String value) {
        this.value = value;
    }
}

package com.example.likelionfinalproject.enums;

import lombok.Getter;

@Getter
public enum AlarmTestEssentials {
    ALARM_URL("/api/v1/alarms"),
    ALARM_TITLE("포스트 제목"),
    ALARM_BODY("포스트 내용"),
    ALARM_ID("1"),
    ALARM_TYPE("NEW_LIKE_ON_POST"),
    FROM_USER("1"),
    TARGET_USER("1"),
    TEXT("new like!");

    private final String value;
    AlarmTestEssentials(String value) {
        this.value = value;
    }
}

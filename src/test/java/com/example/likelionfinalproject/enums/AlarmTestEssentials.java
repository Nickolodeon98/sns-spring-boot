package com.example.likelionfinalproject.enums;

public enum AlarmTestEssentials {
    ALARM_URL("/api/v1/alarms"),
    ALARM_TITLE("포스트 제목"),
    ALARM_BODY("포스트 내용");

    private final String value;
    AlarmTestEssentials(String value) {
        this.value = value;
    }
}

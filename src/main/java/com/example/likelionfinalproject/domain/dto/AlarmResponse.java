package com.example.likelionfinalproject.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class AlarmResponse {
    private Integer id;
    private String alarmType;
    private Integer fromUserId;
    private Integer targetId;
    private String text;
    private LocalDateTime createdAt;
}

package com.example.likelionfinalproject.domain.dto.request;

import com.example.likelionfinalproject.domain.dto.AlarmType;
import com.example.likelionfinalproject.domain.entity.Alarm;
import com.example.likelionfinalproject.domain.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AlarmRequest {

    private AlarmType alarmType;
    private Integer fromUserId;
    private Integer targetId;
    private String text;


    public Alarm toEntity(UserEntity userEntity) {
        return Alarm.builder()
                .userEntity(userEntity)
                .alarmType(alarmType.name())
                .fromUserId(fromUserId)
                .targetId(targetId)
                .text(text)
                .build();
    }
}

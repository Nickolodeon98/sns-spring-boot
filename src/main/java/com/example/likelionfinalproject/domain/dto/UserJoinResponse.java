package com.example.likelionfinalproject.domain.dto;

import com.example.likelionfinalproject.domain.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserJoinResponse {

    private Integer userId;
    private String userName;

    public static UserJoinResponse of(UserEntity userEntity) {
        return new UserJoinResponse(userEntity.getId(), userEntity.getUserName());
    }
}

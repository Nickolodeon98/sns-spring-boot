package com.example.likelionfinalproject.domain.dto;

import com.example.likelionfinalproject.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserJoinResponse {

    private Integer userId;
    private String userName;

    public static UserJoinResponse of(User user) {
        return new UserJoinResponse(user.getId(), user.getUserName());
    }
}

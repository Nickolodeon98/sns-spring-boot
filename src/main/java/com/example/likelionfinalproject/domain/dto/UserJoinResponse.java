package com.example.likelionfinalproject.domain.dto;

import com.example.likelionfinalproject.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserJoinResponse {

    private String userId;
    private String message;

    public static UserJoinResponse of(User user) {
        return new UserJoinResponse(user.getUserId(), "회원가입에 성공했습니다.");
    }
}
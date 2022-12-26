package com.example.likelionfinalproject.domain.dto;

import com.example.likelionfinalproject.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class UserJoinRequest {

    private String userId;
    private String password;


    public User toEntity(String password) {
        return User.builder().userId(this.userId).password(password).build();
    }
}

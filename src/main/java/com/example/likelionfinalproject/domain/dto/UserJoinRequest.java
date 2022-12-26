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

    private String userName;
    private String password;


    public User toEntity(String password) {
        return User.builder().userName(this.userName).password(password).build();
    }
}

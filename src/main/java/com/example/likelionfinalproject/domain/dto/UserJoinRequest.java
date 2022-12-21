package com.example.likelionfinalproject.domain.dto;

import com.example.likelionfinalproject.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserJoinRequest {

    private String name;
    private String userId;
    private String password;


    public User toEntity() {
        return User.builder().userId(this.userId).password(this.password).name(this.name).build();
    }
}

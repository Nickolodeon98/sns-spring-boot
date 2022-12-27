package com.example.likelionfinalproject.domain.dto;

import com.example.likelionfinalproject.domain.entity.User;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Setter
@NoArgsConstructor
public class UserJoinRequest {

    private String userName;
    private String password;


    public User toEntity(String password) {
        return User.builder().userName(this.userName).password(password).build();
    }
}

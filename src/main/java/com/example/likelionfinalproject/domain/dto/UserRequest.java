package com.example.likelionfinalproject.domain.dto;

import com.example.likelionfinalproject.domain.entity.UserEntity;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Setter
@NoArgsConstructor
public class UserRequest {

    private String userName;
    private String password;


    public UserEntity toEntity(String password) {
        return UserEntity.builder().userName(this.userName).password(password).build();
    }
}

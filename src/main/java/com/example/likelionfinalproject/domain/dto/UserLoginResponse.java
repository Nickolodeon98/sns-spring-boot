package com.example.likelionfinalproject.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserLoginResponse {

    private String jwt;
}

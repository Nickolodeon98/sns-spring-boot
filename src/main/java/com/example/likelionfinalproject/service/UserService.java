package com.example.likelionfinalproject.service;

import com.example.likelionfinalproject.domain.dto.UserJoinRequest;
import com.example.likelionfinalproject.domain.dto.UserJoinResponse;
import com.example.likelionfinalproject.domain.dto.UserLoginRequest;
import com.example.likelionfinalproject.domain.dto.UserLoginResponse;
import com.example.likelionfinalproject.domain.entity.User;
import com.example.likelionfinalproject.exception.ErrorCode;
import com.example.likelionfinalproject.exception.UserJoinException;
import com.example.likelionfinalproject.repository.UserRepository;
import com.example.likelionfinalproject.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private TokenUtils tokenUtils;

    public UserJoinResponse registerUser(UserJoinRequest userJoinRequest) {

        userRepository.findByUserId(userJoinRequest.getUserId()).ifPresent((user) -> {
            throw new UserJoinException(ErrorCode.DUPLICATE_USERNAME,
                    user.getUserId() + "는 이미 존재하는 아이디입니다.");
        });

        User savedUser = userRepository.save(userJoinRequest.toEntity());

        return UserJoinResponse.of(savedUser);
    }

    public UserLoginResponse verifyUser(UserLoginRequest userLoginRequest) {
        String token = tokenUtils.createToken(userLoginRequest.getUserId());
        return new UserLoginResponse(token);
    }
}
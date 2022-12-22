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

    public UserJoinResponse registerUser(UserJoinRequest userJoinRequest) {

        userRepository.findByUserId(userJoinRequest.getUserId()).ifPresent((user) -> {
            throw new UserJoinException(ErrorCode.DUPLICATE_USERNAME,
                    user.getUserId() + "는 이미 존재하는 아이디입니다.");
        });

        User savedUser = userRepository.save(userJoinRequest.toEntity());

        return UserJoinResponse.of(savedUser);
    }

    public UserLoginResponse verifyUser(UserLoginRequest userLoginRequest) {
        User actualUser = userRepository.findByUserId(userLoginRequest.getUserId())
                .orElseThrow(() -> new UserJoinException(ErrorCode.USERNAME_NOT_FOUND,
                        userLoginRequest.getUserId() + "는 등록되지 않은 아이디입니다."));

        User userTriedWithPassword = userRepository.findByPassword(userLoginRequest.getPassword())
                .orElseThrow(() -> new UserJoinException(ErrorCode.INVALID_PASSWORD,
                        "패스워드가 잘못되었습니다."));

        if (!userTriedWithPassword.getPassword().equals(actualUser.getPassword()))
            throw new UserJoinException(ErrorCode.INVALID_PASSWORD, "패스워드가 잘못되었습니다.");

        String token = TokenUtils.createToken(userLoginRequest.getUserId());
        return new UserLoginResponse(token);
    }
}
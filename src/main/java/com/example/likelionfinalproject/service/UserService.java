package com.example.likelionfinalproject.service;

import com.example.likelionfinalproject.domain.dto.UserJoinRequest;
import com.example.likelionfinalproject.domain.dto.UserJoinResponse;
import com.example.likelionfinalproject.domain.dto.UserLoginRequest;
import com.example.likelionfinalproject.domain.dto.UserLoginResponse;
import com.example.likelionfinalproject.domain.entity.User;
import com.example.likelionfinalproject.exception.ErrorCode;
import com.example.likelionfinalproject.exception.UserException;
import com.example.likelionfinalproject.repository.UserRepository;
import com.example.likelionfinalproject.utils.TokenUtils;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret.key}")
    private String secretKey;

    public UserJoinResponse registerUser(UserJoinRequest userJoinRequest) {

        userRepository.findByUserName(userJoinRequest.getUserName()).ifPresent((user) -> {
            throw new UserException(ErrorCode.DUPLICATE_USERNAME,
                    user.getUserName() + "는 이미 존재하는 아이디입니다.");
        });

        log.info("userName:{}", userJoinRequest.getUserName());
        log.info("password:{}", userJoinRequest.getPassword());

        User savedUser = userRepository
                .save(userJoinRequest
                        .toEntity(encoder.encode(userJoinRequest.getPassword())));

        return UserJoinResponse.of(savedUser);
    }

    public UserLoginResponse verifyUser(UserLoginRequest userLoginRequest) {
        User user = userRepository.findByUserName(userLoginRequest.getUserName())
                .orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND,
                        userLoginRequest.getUserName() + "는 등록되지 않은 아이디입니다."));

        log.info("userName:{}", userLoginRequest.getUserName());
        log.info("password:{}", userLoginRequest.getPassword());

        String password = userLoginRequest.getPassword();

        if (!encoder.matches(password, user.getPassword()))
            throw new UserException(ErrorCode.INVALID_PASSWORD, "패스워드가 잘못되었습니다.");

        String token = TokenUtils.createToken(userLoginRequest.getUserName(), secretKey);
        return new UserLoginResponse(token);
    }
}
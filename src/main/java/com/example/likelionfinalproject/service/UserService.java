package com.example.likelionfinalproject.service;

import com.example.likelionfinalproject.domain.dto.UserJoinRequest;
import com.example.likelionfinalproject.domain.dto.UserJoinResponse;
import com.example.likelionfinalproject.domain.entity.User;
import com.example.likelionfinalproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public UserJoinResponse registerUser(UserJoinRequest userJoinRequest) {
        User savedUser = userRepository.save(userJoinRequest.toEntity());

        return UserJoinResponse.of(savedUser);
    }
}
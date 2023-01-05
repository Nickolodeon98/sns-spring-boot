package com.example.likelionfinalproject.service;

import com.example.likelionfinalproject.domain.entity.User;
import com.example.likelionfinalproject.exception.ErrorCode;
import com.example.likelionfinalproject.exception.UserException;
import com.example.likelionfinalproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    public String pushThumbsUp(Integer postId, String userName) {

        User user = userRepository.findByUserName(userName)
                .orElseThrow(()->new UserException(ErrorCode.USERNAME_NOT_FOUND, ErrorCode.USERNAME_NOT_FOUND.getMessage()))

        Like like = Like.builder()
                .postId(postId)
                .userId(user.getId())
                .build();
        likeRepository.save(like);

        return "좋아요를 눌렀습니다.";
    }
}

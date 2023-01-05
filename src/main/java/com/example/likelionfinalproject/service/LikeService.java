package com.example.likelionfinalproject.service;

import com.example.likelionfinalproject.domain.entity.Like;
import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.User;
import com.example.likelionfinalproject.exception.ErrorCode;
import com.example.likelionfinalproject.exception.UserException;
import com.example.likelionfinalproject.repository.LikeRepository;
import com.example.likelionfinalproject.repository.PostRepository;
import com.example.likelionfinalproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public String pushThumbsUp(Integer postId, String userName) {

        /* 사용자가 현재 요청된 포스트에 좋아요를 이미 눌렀을 때, 한 번 더 누른 상황이면 좋아요가 삭제된다. */

        User user = userRepository.findByUserName(userName)
                .orElseThrow(()->new UserException(ErrorCode.USERNAME_NOT_FOUND, ErrorCode.USERNAME_NOT_FOUND.getMessage()));

        Post post = postRepository.findById(postId)
                .orElseThrow(()->new UserException(ErrorCode.POST_NOT_FOUND, ErrorCode.POST_NOT_FOUND.getMessage()));

        /* DDD (Domain Driven Development) 를 적용하면 엔티티 내에서 빌더 패턴으로 엔티티를 생성할 수도 있다. */
        Like like = Like.builder()
                .post(post)
                .user(user)
                .build();

        likeRepository.save(like);

        return "좋아요를 눌렀습니다.";
    }
}

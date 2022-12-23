package com.example.likelionfinalproject.service;

import com.example.likelionfinalproject.domain.dto.PostRequest;
import com.example.likelionfinalproject.domain.dto.PostResponse;
import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.User;
import com.example.likelionfinalproject.exception.ErrorCode;
import com.example.likelionfinalproject.exception.UserException;
import com.example.likelionfinalproject.repository.PostRepository;
import com.example.likelionfinalproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostResponse createNewPost(PostRequest postRequest, String authorId) {
        Post post = postRequest.toEntity();
        User user = userRepository.findByUserId(authorId)
                .orElseThrow(()->new UserException(ErrorCode.USERNAME_NOT_FOUND, authorId + "은 없는 아이디입니다."));
        // TODO: 이후에 에러를 어떻게 처리할 지 생각
        // 필터에서 다 걸러져서 아이디가 오는 것이라 아이디가 없을 수는 없음!!!

        post.setAuthor(user);

        Post savedPost = postRepository.save(post);

        return PostResponse.of(savedPost);
    }
}

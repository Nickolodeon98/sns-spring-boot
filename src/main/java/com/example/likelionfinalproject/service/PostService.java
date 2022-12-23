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

    public PostResponse createNewPost(PostRequest postRequest) {
        Post post = postRequest.toEntity();
        User user = userRepository.findByName(postRequest.getAuthor())
                .orElseThrow(()->new UserException(ErrorCode.USERNAME_NOT_FOUND, postRequest.getAuthor() + "은 없는 아이디입니다."));

        post.setAuthor(user);

        Post savedPost = postRepository.save(post);

        return PostResponse.of(savedPost);
    }
}

package com.example.likelionfinalproject.service;

import com.example.likelionfinalproject.domain.dto.PostRequest;
import com.example.likelionfinalproject.domain.dto.PostResponse;
import com.example.likelionfinalproject.domain.dto.SelectedPostResponse;
import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.User;
import com.example.likelionfinalproject.exception.ErrorCode;
import com.example.likelionfinalproject.exception.UserException;
import com.example.likelionfinalproject.repository.PostRepository;
import com.example.likelionfinalproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostResponse createPost(PostRequest postRequest, String authorId) {
        Post savedPost;
        Post post = postRequest.toEntity();
        User user = userRepository.findByUserName(authorId)
                .orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND, authorId + "은 없는 아이디입니다."));

        post.setAuthor(user);

        savedPost = postRepository.save(post);


        return PostResponse.of(savedPost);
    }

    public SelectedPostResponse acquirePost(Integer postId) {
        Post acquiredPost;

        acquiredPost = postRepository.findById(postId)
                .orElseThrow(() -> new UserException(ErrorCode.POST_NOT_FOUND));


        return SelectedPostResponse.of(acquiredPost);
    }

    public Page<SelectedPostResponse> listAllPosts(Pageable pageable) {

        Page<Post> posts = postRepository.findAll(pageable);

        return new PageImpl<>(posts.stream().map(SelectedPostResponse::of).collect(Collectors.toList()));
    }

    public PostResponse editPost(PostRequest editPostRequest, Integer postId, String currentUser) {
        Post postToUpdate;
        Post editedPost;

        postToUpdate = postRepository.findById(postId)
                .orElseThrow(() -> new UserException(ErrorCode.POST_NOT_FOUND));

        /* 포스트의 작성자로 등록되어 있는 사용자를 못 찾을 때 */
        userRepository.findByUserName(postToUpdate.getAuthor().getUserName())
                .orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND));

        /* 작성자와 사용자가 일치하지 않을 때 */
        if (!currentUser.equals(postToUpdate.getAuthor().getUserName()))
            throw new UserException(ErrorCode.INVALID_PERMISSION);

        editedPost = postRepository.save(editPostRequest.toEntity(postId, postToUpdate.getAuthor()));

        return PostResponse.of(editedPost);
    }

    public PostResponse removePost(Integer postId, String userName) {
        Post post;

        post = postRepository.findById(postId)
                .orElseThrow(() -> new UserException(ErrorCode.POST_NOT_FOUND));

        User user = userRepository.findByUserName(post.getAuthor().getUserName())
                .orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND));

        if (!userName.equals(user.getUserName()))
            throw new UserException(ErrorCode.INVALID_PERMISSION);

        postRepository.deleteById(postId);

        return PostResponse.builder()
                .message("포스트 삭제 완료")
                .postId(postId)
                .build();
    }
}

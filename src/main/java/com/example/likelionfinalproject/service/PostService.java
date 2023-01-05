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


        return PostResponse.of(savedPost, "포스트 등록 완료");
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

    private Post validate(Integer postId, String userName) {
        /* 주어진 고유 번호의 포스트가 존재하지 않을 때 */
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new UserException(ErrorCode.POST_NOT_FOUND));

        /* 포스트의 작성자로 등록되어 있는 사용자를 못 찾을 때 */
        User user = userRepository.findByUserName(post.getAuthor().getUserName())
                .orElseThrow(()-> new UserException(ErrorCode.USERNAME_NOT_FOUND, ErrorCode.USERNAME_NOT_FOUND.getMessage()));

        /* Authentication 에서 가져온 사용자 아이디와 postId 로 찾은 포스트의 사용자 아이디의 일치 여부를 확인한다 */
        if (!userName.equals(user.getUserName()))
            throw new UserException(ErrorCode.INVALID_PERMISSION, ErrorCode.INVALID_PERMISSION.getMessage());

        return post;
    }

    public PostResponse editPost(PostRequest editPostRequest, Integer postId, String currentUser) {
        Post postToUpdate = validate(postId, currentUser);
        Post editedPost = postRepository.save(editPostRequest.toEntity(postId, postToUpdate.getAuthor()));

        return PostResponse.of(editedPost, "포스트 수정 완료");
    }

    public PostResponse removePost(Integer postId, String userName) {
        Post postToDelete = validate(postId, userName);

        postRepository.delete(postToDelete);

        return PostResponse.of(postToDelete, "포스트 삭제 완료");
    }

    public Page<SelectedPostResponse> showMyPosts(String userName) {

        Page<Post> myPosts = postRepository.findAllByAuthorUserName(userName);

        return myPosts.map(SelectedPostResponse::of);
    }
}

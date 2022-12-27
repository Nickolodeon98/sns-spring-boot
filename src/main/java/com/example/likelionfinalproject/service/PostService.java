package com.example.likelionfinalproject.service;

import com.example.likelionfinalproject.domain.dto.EditPostRequest;
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

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostResponse createNewPost(PostRequest postRequest, String authorId) {
        Post post = postRequest.toEntity();
        User user = userRepository.findByUserName(authorId)
                .orElseThrow(()->new UserException(ErrorCode.USERNAME_NOT_FOUND, authorId + "은 없는 아이디입니다."));
        // TODO: 이후에 에러를 어떻게 처리할 지 생각
        // 필터에서 다 걸러져서 아이디가 오는 것이라 아이디가 없을 수는 없음!!!

        post.setAuthor(user);

        Post savedPost = postRepository.save(post);

        return PostResponse.of(savedPost);
    }

    public SelectedPostResponse acquireSinglePost(Integer postId) {
        Optional<Post> acquiredPost = postRepository.findById(postId);
        SelectedPostResponse response = SelectedPostResponse.of(acquiredPost.get());
        return response;
    }

    public Page<SelectedPostResponse> listAllPosts(Pageable pageable) {

        Page<Post> posts = postRepository.findAll(pageable);

        return new PageImpl<>(posts.stream().map(SelectedPostResponse::of).collect(Collectors.toList()));
    }

    public PostResponse editPost(EditPostRequest editPostRequest, Integer postId, String currentUser) {
        Post postToUpdate = postRepository.findById(postId)
                .orElseThrow(()->new UserException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 없습니다."));

        /* 포스트의 작성자로 등록되어 있는 사용자를 못 찾을 때 */
        userRepository.findByUserName(postToUpdate.getAuthor().getUserName())
                .orElseThrow(()->new UserException(ErrorCode.USERNAME_NOT_FOUND, "Not Found"));

        /* 작성자와 사용자가 일치하지 않을 때 */
        if (!currentUser.equals(postToUpdate.getAuthor().getUserName()))
            throw new UserException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다.");

        /* TODO: 데이터베이스 오류가 나는 상황을 처리하는 코드 구현 */


        Post editedPost = postRepository.save(editPostRequest.toEntity(postId, postToUpdate.getAuthor()));

        return PostResponse.of(editedPost);
    }
}

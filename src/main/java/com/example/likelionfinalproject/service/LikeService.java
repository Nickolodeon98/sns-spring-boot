package com.example.likelionfinalproject.service;

import com.example.likelionfinalproject.domain.dto.request.AlarmRequest;
import com.example.likelionfinalproject.domain.dto.AlarmType;
import com.example.likelionfinalproject.domain.entity.LikeEntity;
import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.UserEntity;
import com.example.likelionfinalproject.exception.ErrorCode;
import com.example.likelionfinalproject.exception.UserException;
import com.example.likelionfinalproject.repository.AlarmRepository;
import com.example.likelionfinalproject.repository.LikeRepository;
import com.example.likelionfinalproject.repository.PostRepository;
import com.example.likelionfinalproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final AlarmRepository alarmRepository;

    private Post validate(Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new UserException(ErrorCode.POST_NOT_FOUND));

        if (post.getDeletedAt() != null)
            throw new UserException(ErrorCode.POST_NOT_FOUND);

        return post;
    }

    public String pushThumbsUp(Integer postId, String userName) {

        /* TODO: 사용자가 현재 요청된 포스트에 좋아요를 이미 눌렀을 때, 한 번 더 누른 상황이면 좋아요가 삭제된다.
         * 매개 변수로 받은 포스트 아이디와 사용자 아이디를 사용해서 레코드를 조회했을 때 하나 이상의 레코드가 반환되면
         * 이미 좋아요를 눌렀다는 사실을 확인할 수 있다. */

        UserEntity userEntity = userRepository.findByUserName(userName)
                .orElseThrow(()->new UserException(ErrorCode.USERNAME_NOT_FOUND));

        Post post = validate(postId);

        /* DDD (Domain Driven Development) 를 적용하면 엔티티 내에서 빌더 패턴으로 엔티티를 생성할 수도 있다. */
        LikeEntity likeEntity = LikeEntity.builder()
                .post(post)
                .userEntity(userEntity)
                .build();

        Optional<LikeEntity> duplicateLike = likeRepository.findByPostIdAndUserEntityId(postId, userEntity.getId());

        if (duplicateLike.isPresent() ) {
            if (duplicateLike.get().getDeletedAt() == null) {
                likeRepository.delete(duplicateLike.get());
                return "좋아요를 해제했습니다.";
            }
            likeEntity.setId(duplicateLike.get().getId());
        }
        likeRepository.save(likeEntity);

        AlarmRequest alarmRequest = AlarmRequest.builder()
                .alarmType(AlarmType.NEW_LIKE_ON_POST)
                .fromUserId(userEntity.getId())
                .targetId(post.getAuthor().getId())
                .text("new like!")
                .build();

        alarmRepository.save(alarmRequest.toEntity(userEntity));

        return "좋아요를 눌렀습니다.";
    }

    public Long countLikes(Integer postId) {
        Post post = validate(postId);

        return likeRepository.countByPostId(post.getId());
    }
}

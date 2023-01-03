package com.example.likelionfinalproject.domain.dto;

import com.example.likelionfinalproject.domain.entity.Comment;
import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CommentRequest {

    private String comment;

    public Comment toEntity(Post post, User user) {
        return Comment.builder()
                .comment(comment)
                .postId(post)
                .userId(user)
                .build();
    }
}

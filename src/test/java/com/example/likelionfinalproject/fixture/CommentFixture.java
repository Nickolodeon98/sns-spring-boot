package com.example.likelionfinalproject.fixture;

import com.example.likelionfinalproject.domain.entity.Comment;
import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.UserEntity;

public class CommentFixture {

    public static Comment get(UserEntity user, String message) {
        Post post = PostFixture.get();
        return Comment.builder()
                .id(1)
                .comment(message)
                .post(post)
                .author(user)
                .build();
    }
}

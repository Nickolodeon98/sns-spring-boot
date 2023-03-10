package com.example.likelionfinalproject.repository;

import com.example.likelionfinalproject.domain.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findAllByPostId(Integer postId, Pageable pageable);
}

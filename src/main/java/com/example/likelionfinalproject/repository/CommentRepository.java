package com.example.likelionfinalproject.repository;

import com.example.likelionfinalproject.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}

package com.example.likelionfinalproject.repository;

import com.example.likelionfinalproject.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findAllByAuthorUserName(String userName, Pageable pageable);
}

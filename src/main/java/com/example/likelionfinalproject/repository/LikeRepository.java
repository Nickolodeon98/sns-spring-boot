package com.example.likelionfinalproject.repository;

import com.example.likelionfinalproject.domain.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Integer> {

    Optional<Like> findByPostIdAndUserId(Integer postId, Integer id);
}

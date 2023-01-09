package com.example.likelionfinalproject.repository;

import com.example.likelionfinalproject.domain.entity.LikeEntity;
import com.example.likelionfinalproject.domain.entity.Post;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeEntity, Integer> {

    Optional<LikeEntity> findByPostIdAndUserEntityId(Integer postId, Integer id);
    @Where(clause = "deleted_at IS NULL")
    Optional<LikeEntity> findByPost(Post post);

    @Where(clause = "deleted_at IS NULL")
    long countByPost(Post post);
}

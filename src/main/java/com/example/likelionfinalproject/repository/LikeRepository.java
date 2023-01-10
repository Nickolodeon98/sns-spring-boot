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

    @Query(value = "SELECT l FROM LikeEntity l WHERE l.deletedAt is null AND l.id = ?1")
    @Override
    Optional<LikeEntity> findById(Integer id);

    @Query(value = "SELECT count(l) FROM LikeEntity l WHERE l.deletedAt is null AND l.post.id = ?1")
    long countByPostId(Integer id);
}

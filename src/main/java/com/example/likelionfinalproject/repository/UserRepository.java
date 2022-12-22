package com.example.likelionfinalproject.repository;

import com.example.likelionfinalproject.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);

    Optional<User> findByPassword(String password);
}

package com.example.likelionfinalproject.repository;

import com.example.likelionfinalproject.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}

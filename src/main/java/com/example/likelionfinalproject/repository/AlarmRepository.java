package com.example.likelionfinalproject.repository;


import com.example.likelionfinalproject.domain.entity.Alarm;
import com.example.likelionfinalproject.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Integer> {

    Page<Alarm> findAllByUserEntity(UserEntity user, Pageable pageable);

    Page<Alarm> findAllByTargetId(Integer targetId, Pageable pageable);
}

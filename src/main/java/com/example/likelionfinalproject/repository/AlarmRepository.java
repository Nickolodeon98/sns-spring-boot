package com.example.likelionfinalproject.repository;


import com.example.likelionfinalproject.domain.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Integer> {

}

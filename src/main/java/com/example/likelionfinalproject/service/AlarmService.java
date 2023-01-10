package com.example.likelionfinalproject.service;

import com.example.likelionfinalproject.domain.dto.AlarmResponse;
import com.example.likelionfinalproject.domain.entity.Alarm;
import com.example.likelionfinalproject.domain.entity.UserEntity;
import com.example.likelionfinalproject.exception.ErrorCode;
import com.example.likelionfinalproject.exception.UserException;
import com.example.likelionfinalproject.repository.AlarmRepository;
import com.example.likelionfinalproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;

    private final UserRepository userRepository;


    public Page<AlarmResponse> fetchAllAlarms(Pageable pageable, String userName) {
        UserEntity userEntity = userRepository.findByUserName(userName)
                .orElseThrow(()->new UserException(ErrorCode.USERNAME_NOT_FOUND));

        Page<Alarm> alarms = alarmRepository.findAllByTargetId(userEntity.getId(), pageable);
        return alarms.map(AlarmResponse::of);
    }
}

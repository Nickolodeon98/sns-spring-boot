package com.example.likelionfinalproject.service;

import com.example.likelionfinalproject.domain.dto.AlarmResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;


    public Page<AlarmResponse> fetchAllAlarms(Pageable pageable, String userName) {
        Page<Alarm> alarms = alarmRepository.findAll(pageable);
        return alarms.map(AlarmResponse::of);
    }
}

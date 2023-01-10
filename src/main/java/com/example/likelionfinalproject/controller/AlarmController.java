package com.example.likelionfinalproject.controller;

import com.example.likelionfinalproject.domain.Response;
import com.example.likelionfinalproject.domain.dto.response.AlarmResponse;
import com.example.likelionfinalproject.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AlarmController {

    private final AlarmService alarmService;

    @ResponseBody
    @GetMapping("/alarms")
    public Response<Page<AlarmResponse>> displayAlarms(@ApiIgnore Authentication authentication,
                                                       @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "lastModifiedAt")
                                                 Pageable pageable) {
        Page<AlarmResponse> alarms = alarmService.fetchAllAlarms(pageable, authentication.getName());

        return Response.success(alarms);

    }

}

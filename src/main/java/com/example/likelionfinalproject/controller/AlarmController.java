package com.example.likelionfinalproject.controller;

import com.example.likelionfinalproject.domain.Response;
import com.example.likelionfinalproject.domain.dto.response.AlarmResponse;
import com.example.likelionfinalproject.service.AlarmService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
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
@Api(tags= "ALARM Endpoints")
public class AlarmController {

    private final AlarmService alarmService;

    @Operation(summary = "알람 목록 조회", description= "현재 로그인된 사용자가 작성한 포스트에 발생했던 좋아요와 댓글을 목록으로 볼 수 있다.")
    @ResponseBody
    @GetMapping("/alarms")
    public Response<Page<AlarmResponse>> displayAlarms(@ApiIgnore Authentication authentication,
                                                       @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "lastModifiedAt")
                                                 Pageable pageable) {
        Page<AlarmResponse> alarms = alarmService.fetchAllAlarms(pageable, authentication.getName());

        return Response.success(alarms);

    }

}

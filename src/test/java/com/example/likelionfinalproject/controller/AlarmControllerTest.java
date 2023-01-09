package com.example.likelionfinalproject.controller;

import com.example.likelionfinalproject.enums.TestEssentials;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlarmController.class)
class AlarmControllerTest {

    @MockBean
    AlarmService alarmService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

    }

/*

{
    "resultCode":"SUCCESS",
    "result":{
    "content":
      [
        {
            "id":1,
            "alarmType":"NEW_LIKE_ON_POST",
            "fromUserId":1,
            "targetId":1,
            "text":"new like!",
            "createdAt":"2022-12-25T14:53:28.209+00:00",
        }
      ]
    }
}
 */
    @Nested
    @DisplayName("알람 목록 조회")
    class AlarmsDisplay {

        @Test
        @DisplayName("성공")
        void success_display_alarms() throws Exception {
            AlarmResponse alarmResponse = AlarmResponse.builder()
                    .id()
                    .alarmType()
                    .fromUserId()
                    .targetId()
                    .text()
                    .createdAt()
                    .build();

            Pageable pageable = PageRequest.of(0, 20, Sort.Direction.DESC, "createdAt");

            Page<AlarmResponse> alarms = new PageImpl<>(List.of(alarmResponse));

            given(alarmService.fetchAllAlarms(pageable)).willReturn(alarms);

            mockMvc.perform(get(TestEssentials.ALARM_URL.name()).with(csrf())
                    .content(objectMapper.writeValueAsBytes(pageable)))
                    .andExpect(status().isAccepted())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.content").exists())
                    .andDo(print());

            verify(alarmService).fetchAllAlarms(pageable);
        }
    }


}
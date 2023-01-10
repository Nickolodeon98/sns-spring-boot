package com.example.likelionfinalproject.controller;

import com.example.likelionfinalproject.domain.dto.AlarmResponse;
import com.example.likelionfinalproject.enums.AlarmTestEssentials;
import com.example.likelionfinalproject.service.AlarmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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

    AlarmResponse alarmResponse;

    @Captor
    ArgumentCaptor<Pageable> pageableCaptor;
    Pageable expectedPageable;
    Page<AlarmResponse> alarms;
    @BeforeEach
    void setUp() {
        alarmResponse = AlarmResponse.builder()
                .id(Integer.parseInt(AlarmTestEssentials.ALARM_ID.getValue()))
                .alarmType(AlarmTestEssentials.ALARM_TYPE.getValue())
                .fromUserId(Integer.parseInt(AlarmTestEssentials.FROM_USER.getValue()))
                .targetId(Integer.parseInt(AlarmTestEssentials.TARGET_USER.getValue()))
                .text(AlarmTestEssentials.TEXT.getValue())
                .createdAt(LocalDateTime.of(LocalDate.of(2023, 1, 10), LocalTime.of(10, 10, 10) ))
                .build();

        expectedPageable = PageRequest.of(0, 20, Sort.Direction.DESC, "createdAt");

        alarms = new PageImpl<>(List.of(alarmResponse));
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
        @WithMockUser
        void success_display_alarms() throws Exception {

            given(alarmService.fetchAllAlarms(any(), any())).willReturn(alarms);

            mockMvc.perform(get(AlarmTestEssentials.ALARM_URL.getValue()).with(csrf()))
                    .andExpect(status().isAccepted())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.content").exists())
                    .andDo(print());

            verify(alarmService).fetchAllAlarms(pageableCaptor.capture(), any());

            Pageable apiPageable = pageableCaptor.getValue();

            Assertions.assertEquals(expectedPageable.getPageNumber(), apiPageable.getPageNumber());
            Assertions.assertEquals(expectedPageable.getPageSize(), apiPageable.getPageSize());
            Assertions.assertEquals(expectedPageable.getSort(), apiPageable.getSort());
        }

        @Test
        @DisplayName("실패")
        @WithAnonymousUser
        void fail_display_alarms() throws Exception {
            given(alarmService.fetchAllAlarms(any(), any())).willReturn(alarms);

            mockMvc.perform(get(AlarmTestEssentials.ALARM_URL.getValue()).with(csrf()))
                    .andExpect(status().isUnauthorized())
                    .andDo(print());
        }

    }


}
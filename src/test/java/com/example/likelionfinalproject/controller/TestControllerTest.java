package com.example.likelionfinalproject.controller;

import com.example.likelionfinalproject.service.TestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
class TestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TestService testService;

    int num;
    int result;

    String url;

    @BeforeEach
    void setUp() {
        num = 1234;
        result = 10;
        url = "/api/v1/hello/" + num;
    }

    @Test
    @DisplayName("주어진 각 자릿수의 합을 반환한다.")
    @WithMockUser
    void success_sum_of_digit() throws Exception {
        given(testService.addDigits(num)).willReturn(result);

        mockMvc.perform(get(url).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(testService).addDigits(num);
    }
}
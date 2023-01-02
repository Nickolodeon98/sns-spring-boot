package com.example.likelionfinalproject.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestServiceTest {

    TestService testService;

    @BeforeEach
    void setUp() {
        testService = new TestService();
    }

    @Test
    @DisplayName("각 자릿수의 합을 반환한다.")
    void success_add_digits() {
        Assertions.assertEquals(24, testService.addDigits(6567));
        Assertions.assertEquals(22, testService.addDigits(8923));
        Assertions.assertEquals(36, testService.addDigits(9999));
        Assertions.assertEquals(4, testService.addDigits(1111));
    }

}
package com.example.likelionfinalproject.controller;

import com.example.likelionfinalproject.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@ApiIgnore
public class TestController {

    private final TestService testService;

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "전승환";
    }


    @GetMapping("/bye")
    @ResponseBody
    public String bye() {
        return "bye";
    }

    @GetMapping("/test")
    @ResponseBody
    public String test(Authentication authentication) {
        return authentication.getName();
    }

    @GetMapping("/hello/{num}")
    @ResponseBody
    public String sumOfDigit(@PathVariable int num) {
        int result = testService.addDigits(num);

        return Integer.toString(result);
    }

}

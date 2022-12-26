package com.example.likelionfinalproject.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/v1")
public class TestController {

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "happy_new_year";
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
}

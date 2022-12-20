package com.example.likelionfinalproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1")
public class HelloController {

    @GetMapping("/hello")
    public String helloWorld() {
        return "hello";
    }
}

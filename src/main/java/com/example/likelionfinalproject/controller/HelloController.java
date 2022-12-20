package com.example.likelionfinalproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/v1")
public class HelloController {

    @GetMapping("/hello")
    @ResponseBody
    public String helloWorld() {
        return "hello";
    }


    @GetMapping("/bye")
    @ResponseBody
    public String byeWorld() {
        return "bye";
    }
}

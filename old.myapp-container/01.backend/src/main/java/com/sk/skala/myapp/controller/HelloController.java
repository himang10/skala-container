package com.sk.skala.myapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class HelloController {
    
    @GetMapping("/hello")
    public HelloResponse helloWorld() {
        return new HelloResponse("SKALA에 오신 것을 환영합니다");
    }
}

record HelloResponse(String message) {} 
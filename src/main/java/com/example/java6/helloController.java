package com.example.java6;

import org.springframework.stereotype.Controller;

@Controller
public class helloController {
    public String hello() {
        return "Hello, Spring Boot!";
    }
}

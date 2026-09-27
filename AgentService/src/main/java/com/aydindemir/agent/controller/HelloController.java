package com.aydindemir.agent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "AgentService Hello";
    }

    @GetMapping("/info")
    public String info() {
        return "INFO: AgentService";
    }
}

package com.aydindemir.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/buyer")
public class BuyerController {

    @GetMapping("/hello")
    public String hello() {
        return "BuyerService Hello";
    }

    @GetMapping("/info")
    public String info() {
        return "INFO: BuyerService";
    }
}

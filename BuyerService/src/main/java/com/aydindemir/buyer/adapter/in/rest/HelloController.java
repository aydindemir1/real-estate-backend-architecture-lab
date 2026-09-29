package com.aydindemir.buyer.adapter.in.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "BuyerService Hello";
    }

    @GetMapping("/info")
    public String info() {
        return "INFO: BuyerService";
    }
}

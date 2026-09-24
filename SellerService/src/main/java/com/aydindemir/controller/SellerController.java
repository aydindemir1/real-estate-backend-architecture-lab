package com.aydindemir.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @GetMapping("/hello")
    public String hello() {
        return "SellerService Hello";
    }

    @GetMapping("/info")
    public String info() {
        return "INFO: SellerService";
    }
}

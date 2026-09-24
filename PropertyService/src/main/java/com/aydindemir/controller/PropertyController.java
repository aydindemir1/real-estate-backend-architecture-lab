package com.aydindemir.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/property")
public class PropertyController {

    @GetMapping("/hello")
    public String hello() {
        return "PropertyService Hello";
    }

    @GetMapping("/info")
    public String info() {
        return "INFO: PropertyService";
    }
}

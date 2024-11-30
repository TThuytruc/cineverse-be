package com.hcmus.cineverse_be.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class TestController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Public Endpoint";
    }

    @GetMapping("/private")
    public String privateEndpoint() {
        return "Private Endpoint";
    }
}

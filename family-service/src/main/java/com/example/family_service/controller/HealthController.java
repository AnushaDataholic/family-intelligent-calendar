package com.example.family_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/family")
public class HealthController {
    @GetMapping("/health")
    public String health(){
        return "family service is up";
    }
}

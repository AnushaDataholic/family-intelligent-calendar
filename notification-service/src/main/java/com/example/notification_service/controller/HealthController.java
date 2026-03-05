package com.example.notification_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/notification")
public class HealthController {
    @GetMapping("/health")
    public String health(){
        return "notification service is up";
    }
}

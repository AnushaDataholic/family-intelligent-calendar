package com.example.auth_service.controller;

import com.example.auth_service.dto.request.LoginRequest;
import com.example.auth_service.dto.request.RegisterRequest;
import com.example.auth_service.dto.response.AuthResponse;
import com.example.auth_service.service.AuthService;
import com.example.auth_service.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final AuthService  authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService)
    {
        this.authService = authService;
        this.jwtService = jwtService;

    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request){
        return authService.register(request);
    }
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request){
        return authService.login(request);
    }
    @GetMapping("/me")
    public Map<String, Object> me(@RequestHeader("Authorization") String authorizationHeader){
        String token = authorizationHeader.replace("Bearer","");
        return Map.of(
                "userId", jwtService.getUserIdFromToken(token),
                "email", jwtService.getEmailFromToken(token),
                "role", jwtService.getRoleFromToken(token)
        );
    }

}

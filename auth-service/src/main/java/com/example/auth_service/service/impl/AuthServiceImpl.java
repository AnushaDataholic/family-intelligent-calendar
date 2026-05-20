package com.example.auth_service.service.impl;

import com.example.auth_service.dto.request.LoginRequest;
import com.example.auth_service.dto.request.RegisterRequest;
import com.example.auth_service.dto.response.AuthResponse;
import com.example.auth_service.entity.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.service.AuthService;
import com.example.auth_service.service.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse register(RegisterRequest request){
        if(userRepository.existsByEmail((request.getEmail()))){
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole().toUpperCase());
        user.setActive(true);

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved);
        return new AuthResponse("user registered successfully", saved.getId(), saved.getEmail(),saved.getRole(), token);


    }

    @Override
    public AuthResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if(!passwordEncoder.matches(request.getPassword(),user.getPasswordHash())){
            throw new RuntimeException("Invalid email or password");

        }
        String token = jwtService.generateToken(user);
        return new AuthResponse("Login Successful", user.getId(), user.getEmail(),user.getRole(), token);
    }
}

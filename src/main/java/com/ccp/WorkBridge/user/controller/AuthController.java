package com.ccp.WorkBridge.user.controller;

import com.ccp.WorkBridge.dto.AuthResponse;
import com.ccp.WorkBridge.dto.LoginRequest;
import com.ccp.WorkBridge.dto.RegisterRequest;
import com.ccp.WorkBridge.user.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public AuthResponse registerUser(@RequestBody RegisterRequest request) {
        return service.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return service.login(request);
    }

}

package com.taskmanager.controller;

import com.taskmanager.dto.LoginResponseDTO;
import com.taskmanager.dto.LoginRequestDTO;
import com.taskmanager.dto.UserRequestDTO;
import com.taskmanager.dto.UserResponseDTO;
import com.taskmanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user registration and login")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Register new user", description = "Creates a new user account")
    @PostMapping("/register")
    public UserResponseDTO registerUser(@Valid @RequestBody UserRequestDTO request) {
        return userService.registerUser(request);
    }

    @Operation(summary = "User login", description = "Authenticates user and returns JWT token")
    @PostMapping("/login")
    public LoginResponseDTO loginUser(@Valid @RequestBody LoginRequestDTO request) {
        return userService.loginUser(request);
    }

    @Operation(summary = "Test protected API", description = "Check if authentication is working")
    @GetMapping("/test")
    public String test() {
        return "Protected API working";
    }
}
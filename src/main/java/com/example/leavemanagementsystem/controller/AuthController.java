package com.example.leavemanagementsystem.controller;

import com.example.leavemanagementsystem.dto.JwtResponseDto;
import com.example.leavemanagementsystem.dto.LoginRequestDto;
import com.example.leavemanagementsystem.dto.SignupRequestDto;
import com.example.leavemanagementsystem.model.User;
import com.example.leavemanagementsystem.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private UserService userService;

    // Login - Cache token in Redis
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequest) {
        JwtResponseDto jwt = userService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwt);
    }

    // Signup - Register new user
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequestDto signupRequest) {
        User user = userService.registerUser(signupRequest);
        return ResponseEntity.ok("User registered successfully: " + user.getUsername());
    }

    // Logout - Remove token from Redis
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        userService.logout(token);
        return ResponseEntity.ok("User logged out successfully.");
    }

    // Session validation - Check if token exists in Redis
    @GetMapping("/validate-session")
    public ResponseEntity<?> validateSession(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        JwtResponseDto cachedSession = userService.validateSession(token);

        if (cachedSession != null) {
            return ResponseEntity.ok("Session is valid.");
        } else {
            return ResponseEntity.status(401).body("Session expired or invalid token.");
        }
    }
}
